package io.sedu.mc.parties.network;

import io.sedu.mc.parties.data.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.UUID;

import static io.sedu.mc.parties.data.Util.*;

public class ServerPacketHelper {

    public static void sendNewMember(UUID futureMember, ArrayList<UUID> party) {
        //Send each member to future party member.
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, party), getServerPlayer(futureMember));

        //Send future member to each party member.
        party.forEach(id -> {
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, futureMember), getServerPlayer(id));
            //Tell newly online player that this other member is also online.
            if (isOnline(id))
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, id), getServerPlayer(futureMember));
        });

        //Send leader to future party member.
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
        }
    }

    public static void sendOffline(UUID player) {
        if (Util.hasParty(player)) {
            Util.getPartyFromMember(player).getMembers().forEach(id -> {
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(1, player), getServerPlayer(id));
            });
        }
    }
}
