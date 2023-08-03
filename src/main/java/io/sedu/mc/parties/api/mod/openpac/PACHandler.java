package io.sedu.mc.parties.api.mod.openpac;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyDisbandEvent;
import io.sedu.mc.parties.api.events.PartyLeaveEvent;
import io.sedu.mc.parties.api.helper.PartyAPI;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.data.*;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
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

import static io.sedu.mc.parties.data.PartyData.partyList;
import static io.sedu.mc.parties.data.PartySaveData.server;

public class PACHandler implements IPACHandler {

    @Override
    public void initParties(MinecraftServer server) {
        Parties.LOGGER.info("[Parties] Loading parties from Open Parties and Claims.");
        Parties.LOGGER.debug("Current Party Size: " + partyList.size());
        OpenPACServerAPI.get(server).getPartyManager().getAllStream().forEach(party -> {
            UUID partyId = party.getId();
            //Create new party.
            PartyData pData = new PartyData(partyId, party.getOwner().getUUID(), false); //All members are added later.
            //For each member.
            party.getMemberInfoStream().forEach(member -> {
                UUID pId = member.getUUID();
                //Create member.
                new ServerPlayerData(pId, partyId, member.getUsername());
                //Then add to party.
                pData.addMemberSilently(pId);
            });
            //Then save the party.
            partyList.put(partyId, pData);
        });
        PartySaveData.get().setDirty();
        Parties.LOGGER.info("[Parties] Load complete!");
    }



    @Override
    public void memberAdded(UUID owner, UUID newMember, UUID newPartyId) {
        if (owner == newMember) {
            //Silently creating new party.
            Parties.LOGGER.debug("Silently creating party of one member...");
            PlayerAPI.getPlayer(owner, p -> p.addParty(newPartyId));
            PartyData pData = new PartyData(newPartyId, owner, true);
            partyList.put(newPartyId, pData);
            ServerPacketHelper.sendNewLeader(owner);
            PartySaveData.get().setDirty();
            return;
        }
        if (!PartyAPI.hasParty(owner) || PartyAPI.hasParty(newMember)) {
            syncParties();
            return;
        }
        ServerPlayer p = PlayerAPI.getNormalServerPlayer(newMember);
        if (p != null) {
            PartyHelper.addPlayerToParty(newMember, Objects.requireNonNull(PartyAPI.getPartyFromMember(owner)));
            PartySaveData.get().setDirty();
            if (server != null) {
                server.getCommands().sendCommands(p);
            }
        } else {
            Parties.LOGGER.error("[Parties] Error adding new player to party - player doesn't exist! - " + newMember);
        }


    }

    @Override
    public void memberLeft(UUID memberLeft) {
        PartyHelper.removePlayerFromParty(memberLeft, false);
        PartySaveData.get().setDirty();
        PlayerAPI.getServerPlayer(memberLeft, m -> {
            if (server != null) {
                server.getCommands().sendCommands(m);
            }
        });
    }

    @Override
    public void memberKicked(UUID owner, UUID memberLeft, UUID partyId) {
        //Player is being removed.
        MinecraftForge.EVENT_BUS.post(new PartyLeaveEvent(memberLeft, partyId));
        if (owner == memberLeft) {
            //Silently removing party.
            partyList.remove(partyId);
            ServerPlayerData.playerList.get(owner).removeParty();
            //Party is being disbanded.
            MinecraftForge.EVENT_BUS.post(new PartyDisbandEvent(partyId));
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(6), PlayerAPI.getNormalServerPlayer(owner));
            PartySaveData.get().setDirty();
            return;
        }
        PartyHelper.removePlayerFromParty(memberLeft, true);
        PartySaveData.get().setDirty();
        PlayerAPI.getServerPlayer(memberLeft, m -> {
            if (server != null) {
                server.getCommands().sendCommands(m);
            }
        });
    }

    @Override
    public void changeLeader(UUID owner, UUID newLeader) {
        if (owner == newLeader) {
            Parties.LOGGER.error("[Parties] Attempting to set new leader but it's the same one...");
            return;
        }
        if (!PartyAPI.hasParty(owner) || !PartyAPI.inSameParty(owner, newLeader)) {
            syncParties();
        } else {
            Objects.requireNonNull(PartyAPI.getPartyFromMember(owner)).updateLeader(newLeader);
            PartySaveData.get().setDirty();
            PlayerAPI.getServerPlayer(newLeader, m -> {
                if (server != null) {
                    server.getCommands().sendCommands(m);
                }
            });
            PlayerAPI.getServerPlayer(owner, m -> {
                if (server != null) {
                    server.getCommands().sendCommands(m);
                }
            });
        }

    }

    @Override
    public void disbandParty(UUID partyId) {
        PartyData p = PartyAPI.getPartyFromId(partyId);
        if (p != null)
            p.disband();
        PartySaveData.get().setDirty();
    }

    @Override
    public boolean addPartyMember(UUID initiator, UUID futureMember, boolean finalAttempt) {
        AtomicBoolean success = new AtomicBoolean(false);
        getPM(pm -> {
            IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party;
            ServerPlayer owner = PlayerAPI.getNormalServerPlayer(initiator);
            if (!pm.partyExistsForOwner(initiator) && owner != null) {
                party = pm.createPartyForOwner(owner);
            } else {
                party = pm.getPartyByOwner(initiator);
            }
            if (party != null) {
                ServerPlayerData fM = PlayerAPI.getNormalPlayer(futureMember);
                if (fM != null) {
                    //Normal Player would never be null here...
                    success.set(party.addMember(futureMember, PartyMemberRank.MEMBER, Objects.requireNonNull(fM.getName())) != null);
                }
            }
        });
        if (!success.get()) {
            if (finalAttempt) {
                Parties.LOGGER.error("[Parties] Still failed to add member to party. Aborting...");
                return false;
            } else {
                Parties.LOGGER.error("[Parties] Error adding a member to party! Assuming there's a party desync...");
                syncParties();
                Parties.LOGGER.error("[Parties] Trying to add member again...");
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
                Parties.LOGGER.error("[Parties] Still failed to remove member from party. Aborting...");
                return false;
            } else {
                Parties.LOGGER.error("[Parties] Error removing member from party! Assuming there's a party desync...");
                syncParties();
                Parties.LOGGER.error("[Parties] Trying to remove member again...");
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
        PartyData partyData = PartyAPI.getPartyFromMember(newLeader);
        IPartyMember newOwner;
        //Verify old leader from both parties are the same.
        if (pacParty != null && partyData != null && pacParty.getOwner().getUUID().equals(partyData.getLeader()) && (newOwner = pacParty.getMemberInfo(newLeader)) != null) { //Parties exist, Same Party, Same Leader
            pacParty.changeOwner(newLeader, newOwner.getUsername());
            success = true;
        }
        if (!success) {
            if (finalAttempt) {
                Parties.LOGGER.error("[Parties] Still failed to change party leader. Aborting...");
                return false;
            } else {
                Parties.LOGGER.error("[Parties] Error changing party leader! Assuming there's a party desync...");
                syncParties();
                Parties.LOGGER.error("[Parties] Trying to change leader again...");
                return changePartyLeader(newLeader, true);
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean partyMemberLeft(UUID memberLeaving, boolean finalAttempt) {
        //Check if leaving member is the leader
        PartyData partyData = PartyAPI.getPartyFromMember(memberLeaving);
        ServerPlayerData serverPlayerData = PlayerAPI.getNormalPlayer(memberLeaving);
        UUID curLeader;
        if (serverPlayerData != null && partyData != null) {
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
            Parties.LOGGER.error("[Parties] Server Data was never saved. Party sync cannot be completed...");
        } else {
            action.accept(OpenPACServerAPI.get(server).getPartyManager());
        }
    }

    private static void syncParties() {
        Parties.LOGGER.error("[Parties] Parties between mods are desynced! Attempting to recreate...");
        HashMap<UUID, PartyData> updatedParties = new HashMap<>();
        ServerPlayerData.playerList.forEach((uuid, playerData) -> {
            //Player is being removed.
            MinecraftForge.EVENT_BUS.post(new PartyLeaveEvent(uuid, playerData.getPartyId()));
            playerData.removeParty(); //Remove parties from all current players.
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(6), PlayerAPI.getNormalServerPlayer(uuid));
        });
        partyList.forEach((uuid, partyData) -> {
            //Player is being removed.
            MinecraftForge.EVENT_BUS.post(new PartyDisbandEvent(uuid));
        });
        partyList.clear();
        getPM(pm -> {
            pm.getAllStream().forEach(party -> {
                UUID partyId = party.getId();
                //Create new party.
                PartyData pData = new PartyData(partyId, party.getOwner().getUUID(), false);
                //For each member.
                party.getMemberInfoStream().forEach(member -> {
                    UUID pId = member.getUUID();
                    //Create member.
                    ServerPlayerData pD = ServerPlayerData.playerList.get(pId);
                    if (pD != null) {
                        pD.addParty(partyId);
                        pD.setName(member.getUsername());
                    } else {
                        new ServerPlayerData(pId, partyId, member.getUsername());
                    }

                    //Then add to party.
                    pData.addMemberSilently(pId);
                });
                //Then save the party.
                updatedParties.put(partyId, pData);
            });
            partyList = updatedParties;
            PartySaveData.get().setDirty();
            partyList.forEach((partyId, partyData) -> partyData.getMembers().forEach(member -> {
                ServerPlayer mS = PlayerAPI.getNormalServerPlayer(member);
                ArrayList<UUID> mParty = new ArrayList<>(partyData.getMembers());
                mParty.remove(member);
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, mParty), mS);
                mParty.forEach(pMember -> {
                    if (PlayerAPI.isOnline(pMember)) {//Is online
                        PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, pMember), mS);
                        //Send other data
                        InfoPacketHelper.forceUpdate(member, pMember, true);
                        //TODO: Make this more efficient. This sends data multiple times...
                        //API Helper
                        PlayerAPI.getServerPlayer(pMember, (player) -> MinecraftForge.EVENT_BUS.post(new PartyJoinEvent(player)));
                    } else {
                        PartiesPacketHandler.sendToPlayer(new ClientPacketData(1, pMember), mS);
                    }
                });
            }));
            PartySaveData.get().setDirty();
        });
    }
}
