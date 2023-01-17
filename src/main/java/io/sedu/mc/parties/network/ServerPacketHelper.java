package io.sedu.mc.parties.network;

import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.data.Util;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.UUID;

import static io.sedu.mc.parties.data.Util.*;

public class ServerPacketHelper {

    public static void sendNewMember(UUID futureMember, ArrayList<UUID> party) {
        //Send each member to future party member.
        System.out.println("Sending to player #1");
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, party), getServerPlayer(futureMember));
        //Send each member's names to future party member.
        party.forEach(id -> {
            InfoPacketHelper.sendName(futureMember, id);
        });

        //Send future member to each party member.
        party.forEach(id -> {
            System.out.println("Sending to player loop");
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, futureMember), getServerPlayer(id));
            //Send future member name to each party member.
            InfoPacketHelper.sendName(id, futureMember);
            //Tell party member that new member is Online
            if (isOnline(futureMember))
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, futureMember), getServerPlayer(id));
            //Tell newly online player that this other member is also online.
            if (isOnline(id))
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, id), getServerPlayer(futureMember));
        });

        //Send leader to future party member.
        System.out.println("Sending to player #2");
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(3, getPartyFromMember(party.get(0)).getLeader()),
                                          getServerPlayer(futureMember));
    }

    public static void sendRemoveMember(UUID removedMember, ArrayList<UUID> party, boolean wasKicked) {
        int i = (wasKicked) ? 5 : 4;
        party.forEach(id -> {
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(i, removedMember), getServerPlayer(id));
        });
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(i), getServerPlayer(removedMember));
    }

    public static void disband(ArrayList<UUID> party) {
        party.forEach(id -> {
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(6), getServerPlayer(id));
        });
    }

    public static void sendNewLeader(UUID newLeader, ArrayList<UUID> party) {
        party.forEach(id -> {
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(3, newLeader), getServerPlayer(id));
        });
    }

    public static void sendOnline(ServerPlayer player) {
        if (Util.hasParty(player.getUUID())) {
            ArrayList<UUID> mParty = new ArrayList<>(Util.getPartyFromMember(player.getUUID()).getMembers());
            mParty.remove(player.getUUID());
            //Pretend the party just formed and add all players for new online member.
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, mParty), player);
            mParty.forEach(id -> {
                ServerPlayer p = getServerPlayer(id);
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, player.getUUID()), getServerPlayer(id));
                //Tell newly online player that this other member is also online.
                if (isOnline(id))
                    PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, id), player);
            });
            //Tell the online party member who the leader is.
            PartiesPacketHandler.sendToPlayer(
                    new ClientPacketData(3, getPartyFromMember(player.getUUID()).getLeader()), player);
        }
    }

    public static void sendOffline(UUID player) {
        if (Util.hasParty(player)) {
            Util.getPartyFromMember(player).getMembers().forEach(id -> {
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(1, player), getServerPlayer(id));
            });
        }
    }

    public static void trackerToClient(UUID tracker, UUID playerToTrack) {
        System.out.println("Tracker (" + Util.getName(tracker) + ") is now tracking player (" + Util.getName(playerToTrack) +") on client");
        PlayerData.trackerListOld.get(tracker).put(playerToTrack, false);
    }

    public static void trackerToServer(UUID tracker, UUID playerToTrack) {
        System.out.println("Tracker (" + Util.getName(tracker) + ") is now getting tracking info of player (" + Util.getName(playerToTrack)
                                   +") from server");
        PlayerData.trackerListOld.get(tracker).put(playerToTrack, true);
    }

    public static void sendNewLeader(UUID initiator) {
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(3), getServerPlayer(initiator));
    }
}
