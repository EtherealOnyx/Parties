package io.sedu.mc.parties.data;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    public static HashMap<UUID, PlayerData> playerList = new HashMap<>();

    //The UUID of the player we are tracking.
    private UUID player;

    //The UUID of the party that this player belongs to.
    private UUID party;

    private boolean isOnline;

    public PlayerData(UUID id) {
        player = id;
        isOnline = true;
    }

    public UUID getId() {
        return player;
    }

    public boolean hasParty() {
        return party != null;
    }

    public boolean addParty(UUID id) {
        if (party != null)
            return false;
        party = id;
        return true;
    }

    public UUID getPartyId() {
        return party;
    }

    public void setOnline() {
        isOnline = true;
    }

    public void setOffline() {
        isOnline = false;
    }

    public void removeParty() {
        party = null;
    }
}
