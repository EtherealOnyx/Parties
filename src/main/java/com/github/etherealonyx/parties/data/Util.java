package com.github.etherealonyx.parties.data;

import com.github.etherealonyx.parties.data.server.PlayerData;
import com.github.etherealonyx.parties.data.server.ServerData;

import java.util.UUID;

public class Util {

    //This returns the PlayerData of the given UUID.
    public static PlayerData getMember(UUID id) {
        return ServerData.players.get(id);
    }

    //This simply checks if both members are in the same party, and if the party even exists in the first place.
    public static boolean inSameParty(UUID... member) {
        PartyData party = getParty(member[0]);
        if (party == null)
            return false;
        for (int i = 1; i < member.length; i++) {
            if (!party.equals(getParty(member[i])))
                return false;
        }
        return true;
    }

    //This gets the PartyData that belongs to the current UUID.
    public static PartyData getParty(UUID id) {
        return getMember(id).getParty();
    }
}
