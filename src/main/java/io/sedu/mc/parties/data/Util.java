package io.sedu.mc.parties.data;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Util {

    /*
    Server-side Functions
     */
    public static ServerPlayer getServerPlayer(UUID id) {
        PlayerData p;
        if ((p = getPlayer(id)) == null)
            return null;
        return p.serverPlayer == null ? null : p.serverPlayer.get();
    }
    public static PlayerData getPlayer(UUID id) {
        return PlayerData.playerList.get(id);
    }

    public static String getName(UUID id) {
        return getPlayer(id).getName();
    }

    public static PartyData getPartyFromMember(UUID playerId) {
        //Can a hashmap handle a null get()? No...
        UUID party;
        PartyData p;
        if ((party = PlayerData.playerList.get(playerId).getPartyId()) != null && (p = getPartyFromId(party)) != null)
            return p;
        return null;
    }

    public static ArrayList<Player> getOnlineMembersWithoutSelf(UUID memberId) {
        ArrayList<Player> players = new ArrayList<>();
        PartyData p;
        if ((p = getPartyFromMember(memberId)) != null) {
            Player player;
            for (UUID member : p.getMembers()) {
                if (!member.equals(memberId) && (player = getServerPlayer(member)) != null) {
                    players.add(player);
                }
            }
        }
        return players;
    }

    public static ArrayList<Player> getNearMembersWithoutSelf(UUID memberId) {
        ArrayList<Player> players = new ArrayList<>();
        PartyData p;
        if ((p = getPartyFromMember(memberId)) != null) {
            Player player;
            for (UUID member : p.getMembers()) {

                //not self check && is online check && is in range check
                if (!member.equals(memberId) && (player = getServerPlayer(member)) != null && isClientTracked(memberId, member)) {
                    players.add(player);
                }
            }
        }
        return players;
    }

    private static boolean isClientTracked(UUID tracker, UUID toTrack) {
        HashMap<UUID, Boolean> trackers;
        Boolean serverTracked;
        if ((trackers = PlayerData.playerTrackers.get(toTrack)) != null && (serverTracked = trackers.get(tracker)) != null)
            return !serverTracked;
        return false;
    }

    public static boolean inSameParty(UUID member1, UUID member2) {
        PartyData p1, p2;
        if ((p1 = getPartyFromMember(member1)) == null || (p2 = getPartyFromMember(member2)) == null)
            return false;
        return p1.equals(p2);
    }

    public static PartyData getPartyFromId(UUID partyId) {
        return PartyData.partyList.get(partyId);
    }

    public static boolean isLeader(UUID playerId) {
        PartyData p;
        if ((p = getPartyFromMember(playerId)) != null) {
            return p.isLeader(playerId);
        }
        return false;
    }


    /*
    Client-Side Functions
     */

    public static ClientPlayerData getClientPlayer(UUID player) {
        return ClientPlayerData.playerList.get(player);
    }


    public static boolean hasParty(UUID player) {
        return getPartyFromMember(player) != null;
    }

    public static boolean isOnline(UUID id) {
        return getServerPlayer(id) != null;
    }

}
