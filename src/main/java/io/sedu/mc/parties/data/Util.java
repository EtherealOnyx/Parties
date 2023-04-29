package io.sedu.mc.parties.data;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class Util {

    /*
    Server-side Functions
     */
    public static void getServerPlayer(UUID id, Consumer<ServerPlayer> action) {
        getPlayer(id, (playerData) -> {
            if (playerData.getPlayer() != null)
                action.accept(playerData.getPlayer());
        });
    }

    public static ServerPlayer getNormalServerPlayer(UUID id) {
        PlayerData p;
        return (p = getNormalPlayer(id)) != null ? p.getPlayer() : null;
    }
    public static void getPlayer(UUID id, Consumer<PlayerData> action) {
        PlayerData.playerList.computeIfPresent(id, (uuid, playerData) -> {
            action.accept(playerData);
            return playerData;
        });
    }

    public static @Nullable PlayerData getNormalPlayer(UUID id) {
        return PlayerData.playerList.get(id);
    }

    public static String getName(UUID id) {
        PlayerData p;
        return (p = getNormalPlayer(id)) != null ? p.getName() : "";
    }

    public static PartyData getPartyFromMember(UUID playerId) {
        //Can a hashmap handle a null get()? No...
        UUID party;
        PlayerData pl;
        PartyData p;
        if ((pl = PlayerData.playerList.get(playerId)) != null && (party = pl.getPartyId()) != null && (p = getPartyFromId(party)) != null)
            return p;
        return null;
    }

    public static ArrayList<Player> getOnlineMembersWithoutSelf(UUID memberId) {
        ArrayList<Player> players = new ArrayList<>();
        PartyData p;
        if ((p = getPartyFromMember(memberId)) != null) {
            for (UUID member : p.getMembers()) {
                if (!member.equals(memberId)) {
                    getServerPlayer(member, players::add);
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
                if (!member.equals(memberId) && isClientTracked(memberId, member)) {
                    getServerPlayer(member, players::add);
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

    public static void getClientPlayer(UUID player, Consumer<ClientPlayerData> action) {
        ClientPlayerData p;
        if ((p = ClientPlayerData.playerList.get(player)) != null)
            action.accept(p);
    }


    public static boolean hasParty(UUID player) {
        return getPartyFromMember(player) != null;
    }

    public static boolean isOnline(UUID id) {
        return getNormalServerPlayer(id) != null;
    }

}
