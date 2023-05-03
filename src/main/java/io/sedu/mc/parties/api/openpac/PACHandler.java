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
import xaero.pac.common.server.api.OpenPACServerAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static io.sedu.mc.parties.data.Util.*;

public class PACHandler implements IPACHandler {

    @Override
    public void initParties(MinecraftServer server) {
        OpenPACServerAPI.get(server).getPartyManager().getAllStream().forEach(party -> {
            UUID partyId = party.getId();
            //Create new party.
            PartyData pData = new PartyData(partyId, party.getOwner().getUUID());
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
    }

    @Override
    public void memberAdded(UUID owner, UUID newMember, UUID newPartyId) {
        if (owner == newMember) {
            //Silently creating new party.
            Parties.LOGGER.debug("Silently creating party of one member...");
            Util.getPlayer(owner, p -> p.addParty(newPartyId));
            PartyData pData = new PartyData(newPartyId, owner);
            PartyData.partyList.put(newPartyId, pData);
            ServerPacketHelper.sendNewLeader(owner);
            PartySaveData.get().setDirty();
            return;
        }
        if (!Util.hasParty(owner) || Util.hasParty(newMember)) {
            syncParties(Util.getNormalServerPlayer(owner));
            return;
        }
        if (Util.getNormalServerPlayer(newMember) != null) {
            PartyHelper.addPlayerToParty(newMember, Objects.requireNonNull(Util.getPartyFromMember(owner)));
            PartySaveData.get().setDirty();
        } else {
            Parties.LOGGER.error("Error adding new player to party - player doesn't exist! - " + newMember);
        }

    }

    @Override
    public void memberLeft(UUID memberLeft) {
        PartyHelper.removePlayerFromParty(memberLeft, false);
        PartySaveData.get().setDirty();
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
    }

    @Override
    public void changeLeader(UUID owner, UUID newLeader) {
        if (owner == newLeader) {
            Parties.LOGGER.error("Attempting to set new leader but it's the same one...");
            return;
        }
        if (!Util.hasParty(owner) || !Util.inSameParty(owner, newLeader)) {
            syncParties(Util.getNormalServerPlayer(owner));
        }
        PartyHelper.giveLeader(newLeader);
        PartySaveData.get().setDirty();
    }

    @Override
    public void disbandParty(UUID partyId) {
        PartyData p = Util.getPartyFromId(partyId);
        if (p != null)
            p.disband();
        PartySaveData.get().setDirty();
    }

    @Override
    public void initPartiesSync(MinecraftServer server) {
        PartySaveData.get(); //initialize party data.
        //TODO: Implement party sync for Parties mod.
    }

    private static void syncParties(ServerPlayer p) {
        if (p != null) {
            Parties.LOGGER.error("Parties between mods are desynced! Attempting to recreate...");
            HashMap<UUID, PartyData> updatedParties = new HashMap<>();
            PlayerData.playerList.forEach((uuid, playerData) -> {
                playerData.removeParty(); //Remove parties from all current players.
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(6), getNormalServerPlayer(uuid));
            });
            OpenPACServerAPI.get(p.server).getPartyManager().getAllStream().forEach(party -> {
                UUID partyId = party.getId();
                //Create new party.
                PartyData pData = new PartyData(partyId, party.getOwner().getUUID());
                //For each member.
                party.getMemberInfoStream().forEach(member -> {
                    UUID pId = member.getUUID();
                    //Create member.
                    PlayerData pD = PlayerData.playerList.get(pId);
                    if (pD != null) {
                        pD.addParty(partyId);
                        pD.setName(member.getUsername());
                    } else {
                        new PlayerData(pId, partyId, member.getUsername());                    }

                    //Then add to party.
                    pData.addMemberSilently(pId);
                });
                //Then save the party.
                updatedParties.put(partyId, pData);
            });
            PartyData.partyList = updatedParties;
            PartySaveData.get().setDirty();
            PartyData.partyList.forEach((partyId, partyData) -> {
                partyData.getMembers().forEach(member -> {
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
                            getServerPlayer(pMember, (player) -> {
                                MinecraftForge.EVENT_BUS.post(new PartyJoinEvent(player));
                            });
                        } else {
                            PartiesPacketHandler.sendToPlayer(new ClientPacketData(1, pMember), mS);
                        }
                    });
                });
            });
        }
        PartySaveData.get().setDirty();
    }
}
