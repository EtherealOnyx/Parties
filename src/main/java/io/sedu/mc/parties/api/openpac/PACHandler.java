package io.sedu.mc.parties.api.openpac;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.data.*;
import io.sedu.mc.parties.events.PartyJoinEvent;
import io.sedu.mc.parties.network.ClientPacketData;
import io.sedu.mc.parties.network.InfoPacketHelper;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import io.sedu.mc.parties.network.ServerPacketHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import xaero.pac.common.parties.party.IPartyPlayerInfo;
import xaero.pac.common.parties.party.ally.IPartyAlly;
import xaero.pac.common.parties.party.ally.api.IPartyAllyAPI;
import xaero.pac.common.parties.party.api.IPartyPlayerInfoAPI;
import xaero.pac.common.parties.party.member.IPartyMember;
import xaero.pac.common.parties.party.member.PartyMemberRank;
import xaero.pac.common.parties.party.member.api.IPartyMemberAPI;
import xaero.pac.common.server.ServerData;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.parties.party.IServerParty;
import xaero.pac.common.server.parties.party.api.IPartyManagerAPI;
import xaero.pac.common.server.parties.party.api.IServerPartyAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.sedu.mc.parties.data.PartySaveData.server;
import static io.sedu.mc.parties.data.Util.*;

public class PACHandler implements IPACHandler {

    @Override
    public void initParties(MinecraftServer server) {
        Parties.LOGGER.info("Loading parties from Open Parties and Claims.");
        Parties.LOGGER.debug("Current Party Size: " + PartyData.partyList.size());
        OpenPACServerAPI.get(server).getPartyManager().getAllStream().forEach(party -> {
            UUID partyId = party.getId();
            //Create new party.
            PartyData pData = new PartyData(partyId, party.getOwner().getUUID(), false); //All members are added later.
            //For each member.
            party.getMemberInfoStream().forEach(member -> {
                UUID pId = member.getUUID();
                //Create member.
                new PlayerData(pId, partyId, member.getUsername());
                //Then add to party.
                pData.addMemberSilently(pId);
            });
            //Then save the party.
            PartyData.partyList.put(partyId, pData);
        });
        PartySaveData.get().setDirty();
        Parties.LOGGER.info("Load complete!");
    }



    @Override
    public void memberAdded(UUID owner, UUID newMember, UUID newPartyId) {
        if (owner == newMember) {
            //Silently creating new party.
            Parties.LOGGER.debug("Silently creating party of one member...");
            Util.getPlayer(owner, p -> p.addParty(newPartyId));
            PartyData pData = new PartyData(newPartyId, owner, true);
            PartyData.partyList.put(newPartyId, pData);
            ServerPacketHelper.sendNewLeader(owner);
            PartySaveData.get().setDirty();
            return;
        }
        if (!Util.hasParty(owner) || Util.hasParty(newMember)) {
            syncParties();
            return;
        }
        ServerPlayer p = Util.getNormalServerPlayer(newMember);
        if (p != null) {
            PartyHelper.addPlayerToParty(newMember, Objects.requireNonNull(Util.getPartyFromMember(owner)));
            PartySaveData.get().setDirty();
            if (server != null) {
                server.getCommands().sendCommands(p);
            }
        } else {
            Parties.LOGGER.error("Error adding new player to party - player doesn't exist! - " + newMember);
        }


    }

    @Override
    public void memberLeft(UUID memberLeft) {
        PartyHelper.removePlayerFromParty(memberLeft, false);
        PartySaveData.get().setDirty();
        getServerPlayer(memberLeft, m -> {
            if (server != null) {
                server.getCommands().sendCommands(m);
            }
        });
    }

    @Override
    public void memberKicked(UUID owner, UUID memberLeft, UUID partyId) {
        if (owner == memberLeft) {
            //Silently removing party.
            PartyData.partyList.remove(partyId);
            PlayerData.playerList.get(owner).removeParty();
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(6), getNormalServerPlayer(owner));
            PartySaveData.get().setDirty();
            return;
        }
        PartyHelper.removePlayerFromParty(memberLeft, true);
        PartySaveData.get().setDirty();
        getServerPlayer(memberLeft, m -> {
            if (server != null) {
                server.getCommands().sendCommands(m);
            }
        });
    }

    @Override
    public void changeLeader(UUID owner, UUID newLeader) {
        if (owner == newLeader) {
            Parties.LOGGER.error("Attempting to set new leader but it's the same one...");
            return;
        }
        if (!Util.hasParty(owner) || !Util.inSameParty(owner, newLeader)) {
            syncParties();
        } else {
            Objects.requireNonNull(getPartyFromMember(owner)).updateLeader(newLeader);
            PartySaveData.get().setDirty();
            getServerPlayer(newLeader, m -> {
                if (server != null) {
                    server.getCommands().sendCommands(m);
                }
            });
            getServerPlayer(owner, m -> {
                if (server != null) {
                    server.getCommands().sendCommands(m);
                }
            });
        }

    }

    @Override
    public void disbandParty(UUID partyId) {
        PartyData p = Util.getPartyFromId(partyId);
        if (p != null)
            p.disband();
        PartySaveData.get().setDirty();
    }

    @Override
    public boolean addPartyMember(UUID initiator, UUID futureMember, boolean finalAttempt) {
        AtomicBoolean success = new AtomicBoolean(false);
        getPM(pm -> {
            IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party;
            ServerPlayer owner = getNormalServerPlayer(initiator);
            if (!pm.partyExistsForOwner(initiator) && owner != null) {
                party = pm.createPartyForOwner(owner);
            } else {
                party = pm.getPartyByOwner(initiator);
            }
            if (party != null) {
                PlayerData fM = getNormalPlayer(futureMember);
                if (fM != null) {
                    //Normal Player would never be null here...
                    success.set(party.addMember(futureMember, PartyMemberRank.MEMBER, Objects.requireNonNull(fM.getName())) != null);
                }
            }
        });
        if (!success.get()) {
            if (finalAttempt) {
                Parties.LOGGER.error("Still failed to add member to party. Aborting...");
                return false;
            } else {
                Parties.LOGGER.error("Error adding a member to party! Assuming there's a party desync...");
                syncParties();
                Parties.LOGGER.error("Trying to add member again...");
                return addPartyMember(initiator, futureMember, true);
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean removePartyMember(UUID initiator, UUID removedMember, boolean finalAttempt) {
        AtomicBoolean success = new AtomicBoolean(false);
        getPM(pm -> {
            IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party = pm.getPartyByMember(initiator);
            if (party != null) {
                success.set(party.removeMember(removedMember) != null);
            }
        });
        if (!success.get()) {
            if (finalAttempt) {
                Parties.LOGGER.error("Still failed to remove member from party. Aborting...");
                return false;
            } else {
                Parties.LOGGER.error("Error removing member from party! Assuming there's a party desync...");
                syncParties();
                Parties.LOGGER.error("Trying to remove member again...");
                return removePartyMember(initiator, removedMember, true);
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean changePartyLeader(UUID newLeader, boolean finalAttempt) {
        boolean success = false;
        IServerParty<IPartyMember, IPartyPlayerInfo, IPartyAlly> pacParty = ServerData.from(server).getPartyManager().getPartyByMember(newLeader);
        PartyData partyData = getPartyFromMember(newLeader);
        IPartyMember newOwner;
        //Verify old leader from both parties are the same.
        if (pacParty != null && partyData != null && pacParty.getOwner().getUUID().equals(partyData.getLeader()) && (newOwner = pacParty.getMemberInfo(newLeader)) != null) { //Parties exist, Same Party, Same Leader
            pacParty.changeOwner(newLeader, newOwner.getUsername());
            success = true;
        }
        if (!success) {
            if (finalAttempt) {
                Parties.LOGGER.error("Still failed to change party leader. Aborting...");
                return false;
            } else {
                Parties.LOGGER.error("Error changing party leader! Assuming there's a party desync...");
                syncParties();
                Parties.LOGGER.error("Trying to change leader again...");
                return changePartyLeader(newLeader, true);
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean partyMemberLeft(UUID memberLeaving, boolean finalAttempt) {
        //Check if leaving member is the leader
        PartyData partyData = getPartyFromMember(memberLeaving);
        PlayerData playerData = getNormalPlayer(memberLeaving);
        UUID curLeader;
        if (playerData != null && partyData != null) {
            curLeader = partyData.getLeader();
            //Check if party is a size of 1.
            if (partyData.getMembers().size() == 1) {
                getPM(pm -> {
                    IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party = pm.getPartyByMember(memberLeaving);
                    if (party != null) {
                        pm.removeParty(party);
                    }
                });
            }
            if (curLeader.equals(memberLeaving)) {
                //Transfer ownership first
                curLeader = null;
                for (UUID member : partyData.getMembers()) {
                    if (!member.equals(memberLeaving) && curLeader == null) {
                        //Transfer ownership to first non-same member.
                        curLeader = member;
                    }
                }
                if (curLeader != null) {
                    if (changePartyLeader(curLeader, false)) {
                        //Leader change was successful
                        return removePartyMember(curLeader, memberLeaving, false);
                    }
                }
            } else {
                return removePartyMember(curLeader, memberLeaving, false);
            }
        }
        return false;
    }

    private static void getPM(Consumer<IPartyManagerAPI<IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI>>> action) {
        if (server == null) {
            Parties.LOGGER.error("Server Data was never saved. Party sync cannot be completed...");
        } else {
            action.accept(OpenPACServerAPI.get(server).getPartyManager());
        }
    }

    private static void syncParties() {
        Parties.LOGGER.error("Parties between mods are desynced! Attempting to recreate...");
        HashMap<UUID, PartyData> updatedParties = new HashMap<>();
        PlayerData.playerList.forEach((uuid, playerData) -> {
            playerData.removeParty(); //Remove parties from all current players.
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(6), getNormalServerPlayer(uuid));
        });
        getPM(pm -> {
            pm.getAllStream().forEach(party -> {
                UUID partyId = party.getId();
                //Create new party.
                PartyData pData = new PartyData(partyId, party.getOwner().getUUID(), false);
                //For each member.
                party.getMemberInfoStream().forEach(member -> {
                    UUID pId = member.getUUID();
                    //Create member.
                    PlayerData pD = PlayerData.playerList.get(pId);
                    if (pD != null) {
                        pD.addParty(partyId);
                        pD.setName(member.getUsername());
                    } else {
                        new PlayerData(pId, partyId, member.getUsername());
                    }

                    //Then add to party.
                    pData.addMemberSilently(pId);
                });
                //Then save the party.
                updatedParties.put(partyId, pData);
            });
            PartyData.partyList = updatedParties;
            PartySaveData.get().setDirty();
            PartyData.partyList.forEach((partyId, partyData) -> partyData.getMembers().forEach(member -> {
                ServerPlayer mS = getNormalServerPlayer(member);
                ArrayList<UUID> mParty = new ArrayList<>(partyData.getMembers());
                mParty.remove(member);
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, mParty), mS);
                mParty.forEach(pMember -> {
                    if (isOnline(pMember)) {//Is online
                        PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, pMember), mS);
                        //Send other data
                        InfoPacketHelper.forceUpdate(member, pMember, true);
                        //TODO: Make this more efficient. This sends data multiple times...
                        //API Helper
                        getServerPlayer(pMember, (player) -> MinecraftForge.EVENT_BUS.post(new PartyJoinEvent(player)));
                    } else {
                        PartiesPacketHandler.sendToPlayer(new ClientPacketData(1, pMember), mS);
                    }
                });
            }));
            PartySaveData.get().setDirty();
        });
    }
}
