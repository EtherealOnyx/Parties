package io.sedu.mc.parties.data;

import java.util.UUID;

public class Util {

    /*
    Server-side Functions
     */
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

    public static PlayerData getClientPlayer(UUID id) {
        for (PlayerData data : PlayerData.clientList)
           if (data.getId().equals(id))
               return data;
        return null;
    }





}
