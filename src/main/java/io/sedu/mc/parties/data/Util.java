package io.sedu.mc.parties.data;

import java.util.UUID;

public class Util {

    public static PlayerData getPlayer(UUID id) {
        return PlayerData.playerList.get(id);
    }

    public static PartyData getParty(UUID playerId) {
        //Can a hashmap handle a null get()? No...
        return PartyData.partyList.get(PlayerData.playerList.get(playerId).getPartyId());
    }
}
