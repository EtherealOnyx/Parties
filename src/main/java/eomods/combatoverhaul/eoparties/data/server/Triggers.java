package eomods.combatoverhaul.eoparties.data.server;

import eomods.combatoverhaul.eoparties.network.ClientPacketData;
import eomods.combatoverhaul.eoparties.network.ClientPacketName;
import eomods.combatoverhaul.eoparties.network.Handler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static eomods.combatoverhaul.eoparties.data.server.ServerData.*;
import static eomods.combatoverhaul.eoparties.data.server.Trackers.getTrackers;
import static eomods.combatoverhaul.eoparties.data.server.Util.*;

//Triggers are used anywhere the server is needing to send packet data to the client.
class Triggers {


    static void updateName(UUID entityTracked) {
        System.out.println("Currently cycling through : " + entityTracked + " 's trackers...");
        for (UUID tracker : getTrackers(entityTracked)) {
            System.out.println("Found " + tracker + "....");
            updateName(entityTracked, tracker);
        }
    }

    private static void updateName(UUID entityTracked, UUID tracker) {
        if (isOnline(tracker)) {
            System.out.println("Sending name update: " + entityTracked + " | To: " + tracker);
            Handler.network.sendTo(new ClientPacketName(entityTracked, getName(entityTracked)),
                    getNet(tracker), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    static void markOnline(ServerPlayerEntity playerMP) {
        if (!livingMembers.containsKey(playerMP.getUniqueID())) {
            livingMembers.put(playerMP.getUniqueID(), new LivingMember(playerMP));
        } else {
            livingMembers.get(playerMP.getUniqueID()).setPlayer(playerMP);
        }
        checkName(playerMP);
        Handler.network.sendTo(new ClientPacketData(8), getNet(playerMP), NetworkDirection.PLAY_TO_CLIENT);
    }

    private static void checkName(ServerPlayerEntity playerMP) {
        if (livingMembers.get(playerMP.getUniqueID()).getName().equals(playerMP.getName().getFormattedText()));
    }

    static void markOffline(UUID player) {
        //Server removes tracker, but keeps name.
        if (livingMembers.containsKey(player))
            livingMembers.get(player).setPlayer(null);
        markOffline(player, getParty(player));
    }


    static void updateNameJoin(UUID invitedPlayer) {

        //Send out party members' and pet members' names to invitedPlayer.
        for (UUID partyMember : listWithoutSelf(getParty(invitedPlayer), invitedPlayer)) {
            updateName(partyMember, invitedPlayer);
            for (UUID pet : getSubParty(partyMember))
                updateName(pet, invitedPlayer);
        }

        //Sends out invited player's pet names to the invited player.
        for (UUID pet : getSubParty(invitedPlayer))
            updateName(pet, invitedPlayer);

    }

    static void updatePartyMember(UUID toSend, HashSet<UUID> party, boolean isNew) {
        //Send joining player party member info. Also sends pet info if they have it.
        updatePartyMember(toSend, Util.listWithoutSelf(party, toSend));

        //Send party member player info. Also sends pet info if they have it.
        if (isNew)
            updatePartyMember(Util.listWithoutSelf(party, toSend), toSend);
    }

    private static void updatePartyMember(List<UUID> toSends, UUID player) {
        for (UUID toSend : toSends) {
            updatePartyMember(toSend, player);
            updatePartyMember(toSend, subParties.getOrDefault(player, EMPTY), player);
        }

    }

    static void updatePartyMember(UUID toSend, ArrayList<UUID> party) {

        if (isOnline(toSend))
            Handler.network.sendTo(new ClientPacketData(2, party),
                    getNet(toSend), NetworkDirection.PLAY_TO_CLIENT);

        //Send pets to toSend.
        for (UUID partyMember : party)
            updatePartyMember(toSend, subParties.getOrDefault(partyMember, EMPTY), partyMember);

    }

    static void updatePartyMember(UUID toSend, HashSet<UUID> pets, UUID partyMember) {
        if (pets.size() > 0)
            if (isOnline(toSend))
                Handler.network.sendTo(new ClientPacketData(3, listWithSelf(partyMember, pets)),
                    getNet(toSend), NetworkDirection.PLAY_TO_CLIENT);
    }

    static void updatePartyMember(UUID toSend, UUID partyMember) {
        if (isOnline(toSend)) {
            Handler.network.sendTo(new ClientPacketData(2, partyMember),
                    getNet(toSend), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    static void updateLeader(UUID newLeader, HashSet<UUID> party) {
        //Tell the party about the newLeader.
        for (UUID id : party)
            updateLeader(id, newLeader);
    }

    static void updateLeader(UUID toSend) {
        if (isOnline(toSend))
            Handler.network.sendTo(new ClientPacketData(5), getNet(toSend), NetworkDirection.PLAY_TO_CLIENT);
    }

    static void updateLeader(UUID toSend, UUID newLeader) {
        if (isOnline(toSend)) {
            if (toSend.equals(newLeader))
                updateLeader(toSend);
            else
                //Send leader to connecting client...
                Handler.network.sendTo(new ClientPacketData(5, newLeader), getNet(toSend), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    static void updateOnline(UUID invited, HashSet<UUID> party) {
        for (UUID member : Util.listWithoutSelf(party, invited)) {
            //Tell members that the invited person is online.
            if (isOnline(invited))
                updateOnline(member, invited, true);
            //Tell the invited person that member is online.
            if (isOnline(member))
                updateOnline(invited, member, true);
        }
    }
    static void updateOnline(UUID toSend, UUID onlineMember, boolean isOnline) {
        if (isOnline(toSend))
            Handler.network.sendTo(new ClientPacketData(isOnline ? 0 : 1, onlineMember), getNet(toSend),
                    NetworkDirection.PLAY_TO_CLIENT);
    }

    private static void markOffline(UUID player, HashSet<UUID> party) {
        for (UUID toSend : party)
            updateOnline(toSend, player, false);
    }

    static void moveAllToServer(UUID player) {
        if (isOnline(player)) {
            Trackers.moveAllToServer(player);
            Handler.network.sendTo(new ClientPacketData(7), getNet(player), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static boolean nextLeader(HashSet<UUID> party) {
        for (UUID partyMember : party) {
            if (isOnline(partyMember)) {
                partyLeaders.add(partyMember);
                updateLeader(partyMember, party);
                return true;
            }
        }
        return false;
    }

    public static void sendLeader(UUID invited, HashSet<UUID> partyMembers) {
        updateLeader(invited, getLeader(partyMembers));

    }

    private static UUID getLeader(HashSet<UUID> partyMembers) {
        for (UUID partyMember : partyMembers) {
            if (partyLeaders.contains(partyMember))
                return partyMember;
        }
        return null;
    }

    public static void removeMemberFromParty(UUID playerToRemove, HashSet<UUID> party) {
        for (UUID partyMember : party) {
            removeMemberFromParty(playerToRemove, partyMember);
        }
    }

    static void removeMemberFromParty(UUID droppingPlayer, UUID partyMember) {
        Trackers.removeMemberFromParty(droppingPlayer, partyMember);
        if (isOnline(partyMember))
            Handler.network.sendTo(new ClientPacketData(6, droppingPlayer), getNet(partyMember),
                    NetworkDirection.PLAY_TO_CLIENT);

    }

    static void removeParty(UUID droppingPlayer) {
        Trackers.removeParty(droppingPlayer);
        if (isOnline(droppingPlayer))
            Handler.network.sendTo(new ClientPacketData(6), getNet(droppingPlayer),
                    NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void removeClientTracker(UUID toRemove, UUID clientTracker) {
        if (isOnline(clientTracker))
            Handler.network.sendTo(new ClientPacketData(7, toRemove), getNet(clientTracker),
                    NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void disbandParty(UUID partyMember) {
        partyLeaders.remove(partyMember);
        System.out.println("Sending message to " + getName(partyMember) + " to disband party...");
        //Trackers.removeParty(partyMember); At this point, partyMember shouldn't have any other player trackers...
        if (isOnline(partyMember))
            Handler.network.sendTo(new ClientPacketData(10), getNet(partyMember),
                    NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void removePartyKicked(UUID playerToKick) {
        Trackers.removeParty(playerToKick);
        if (isOnline(playerToKick))
            Handler.network.sendTo(new ClientPacketData(9), getNet(playerToKick),
                    NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void removeMemberFromPartyKicked(UUID playerToKick, UUID partyMember) {
        Trackers.removeMemberFromParty(playerToKick, partyMember);
        if (isOnline(partyMember))
            Handler.network.sendTo(new ClientPacketData(9, playerToKick), getNet(partyMember),
                    NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void removeMemberFromPartyKicked(UUID playerToRemove, HashSet<UUID> party) {
        for (UUID partyMember : party) {
            removeMemberFromPartyKicked(playerToRemove, partyMember);
        }
    }
}
