package io.sedu.mc.parties.data;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class Util {

    /*
    Server-side Functions
     */
    public static ServerPlayer getServerPlayer(UUID id) {
        return getPlayer(id).serverPlayer == null? null : getPlayer(id).serverPlayer.get();
    }
    public static PlayerData getPlayer(UUID id) {
        return PlayerData.playerList.get(id);
    }

    public static PartyData getPartyFromMember(UUID playerId) {
        //Can a hashmap handle a null get()? No...
        return getPartyFromId(PlayerData.playerList.get(playerId).getPartyId());
    }

    public static boolean inSameParty(UUID member1, UUID member2) {
        if (getPartyFromMember(member1) == null)
            return false;
        return getPartyFromMember(member1).equals(getPartyFromMember(member2));
    }

    public static PartyData getPartyFromId(UUID partyId) {
        return PartyData.partyList.get(partyId);
    }

    public static boolean isLeader(UUID playerId) {
        return getPartyFromMember(playerId).isLeader(playerId);
    }


    /*
    Client-Side Functions
     */


    public static boolean hasParty(UUID player) {
        return getPartyFromMember(player) != null;
    }

    public static boolean isOnline(UUID id) {
        return getServerPlayer(id) != null;
    }
}
