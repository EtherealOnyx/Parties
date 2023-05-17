package io.sedu.mc.parties.api.helper;

import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.PlayerData;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PartyAPI {

    public static PartyData getPartyFromMember(UUID playerId) {
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
                    PlayerAPI.getServerPlayer(member, players::add);
                }
            }
        }
        return players;
    }

    public static ArrayList<Player> getNearMembersWithoutSelf(UUID memberId) {
        ArrayList<Player> players = new ArrayList<>();
        PartyData p;
        if ((p = getPartyFromMember(memberId)) != null) {
            for (UUID member : p.getMembers()) {

                //not self check && is online check && is in range check
                if (!member.equals(memberId) && isClientTracked(memberId, member)) {
                    PlayerAPI.getServerPlayer(member, players::add);
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

    public static boolean hasParty(UUID player) {
        return getPartyFromMember(player) != null;
    }
}
