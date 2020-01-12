package eomods.combatoverhaul.eoparties.data.server;

import eomods.combatoverhaul.eoparties.network.COPSHandler;
import eomods.combatoverhaul.eoparties.network.PacketData;
import eomods.combatoverhaul.eoparties.network.PacketName;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.*;

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
            COPSHandler.INSTANCE.sendTo(new PacketName(entityTracked, getName(entityTracked)), getPlayer(tracker));
        }
    }

    static void markOnline(EntityPlayerMP playerMP) {
        if (!livingMembers.containsKey(playerMP.getUniqueID())) {
            livingMembers.put(playerMP.getUniqueID(), new LivingMember(playerMP));
        } else {
            livingMembers.get(playerMP.getUniqueID()).setPlayer(playerMP);
        }
        COPSHandler.INSTANCE.sendTo(new PacketData(8), playerMP);
    }

    static void markOffline(UUID player) {
        //Server removes tracker, but keeps name.
        if (livingMembers.containsKey(player))
            livingMembers.get(player).setPlayer(null);
    }


    static void updateNameForced(UUID invitedPlayer, boolean isNew) {
        //Sends out invited players' name to the listeners.
        updateName(invitedPlayer);

        //Send out party members' and pet members' names to invitedPlayer.
        for (UUID partyMember : listWithoutSelf(getParty(invitedPlayer), invitedPlayer)) {
            updateName(partyMember, invitedPlayer);
            for (UUID pet : getSubParty(partyMember))
                updateName(pet, invitedPlayer);
        }

        if (!isNew)
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
            COPSHandler.INSTANCE.sendTo(new PacketData(2, party),
                    getPlayer(toSend));

        //Send pets to toSend.
        for (UUID partyMember : party)
            updatePartyMember(toSend, subParties.getOrDefault(partyMember, EMPTY), partyMember);

    }

    static void updatePartyMember(UUID toSend, HashSet<UUID> pets, UUID partyMember) {
        if (pets.size() > 0)
            if (isOnline(toSend))
                COPSHandler.INSTANCE.sendTo(new PacketData(3, listWithSelf(partyMember, pets)),
                    getPlayer(toSend));
    }

    static void updatePartyMember(UUID toSend, UUID partyMember) {
        if (isOnline(toSend)) {
            COPSHandler.INSTANCE.sendTo(new PacketData(2, partyMember),
                    getPlayer(toSend));
        }
    }

    static void updateLeader(UUID newLeader, HashSet<UUID> party) {
        //Tell the party about the newLeader.
        for (UUID id : party)
            updateLeader(id, newLeader);
    }

    static void updateLeader(UUID toSend) {
        if (isOnline(toSend))
            COPSHandler.INSTANCE.sendTo(new PacketData(5), getPlayer(toSend));
    }

    static void updateLeader(UUID toSend, UUID newLeader) {
        if (isOnline(toSend)) {
            if (toSend.equals(newLeader))
                updateLeader(toSend);
            else
                //Send leader to connecting client...
                COPSHandler.INSTANCE.sendTo(new PacketData(5, newLeader), getPlayer(toSend));
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
            COPSHandler.INSTANCE.sendTo(new PacketData(isOnline ? 0 : 1, onlineMember), getPlayer(toSend));
    }

    static void removePlayerInfo(UUID player) {
        Trackers.remove(player);
        removePlayerInfo(player, getParty(player));

    }

    private static void removePlayerInfo(UUID player, HashSet<UUID> party) {
        for (UUID toSend : party) {
            if (isOnline(player)) {
                updateOnline(toSend, player, false);
            }
        }
    }

    static void removeClientInfo(UUID toRemove, HashSet<UUID> clientTrackers) {
        for (UUID client : clientTrackers)
            removeClientInfo(toRemove, client);
    }

    static void removeClientInfo(UUID toRemove, UUID clientTracker) {
        if (isOnline(clientTracker))
            COPSHandler.INSTANCE.sendTo(new PacketData(7, toRemove), getPlayer(clientTracker));
    }

    static void moveAllToServer(UUID player) {
        if (isOnline(player))
            COPSHandler.INSTANCE.sendTo(new PacketData(7), getPlayer(player));
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
}
