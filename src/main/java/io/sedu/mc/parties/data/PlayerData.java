package io.sedu.mc.parties.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    public static HashMap<UUID, PlayerData> playerList = new HashMap<>();

    public static ArrayList<PlayerData> clientList;

    public static UUID leader;

    //The UUID of the player we are tracking.
    private UUID player;

    //The UUID of the party that this player belongs to.
    private UUID party;

    //Client-side functionality.
    private boolean isOnline;

    public PlayerData(UUID id) {
        player = id;
        isOnline = true;
        playerList.put(id, this);

    }

    //Client constructor.
    public PlayerData() {

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

    public void setId(UUID uuid) {
        player = uuid;
        if (clientList == null)
            clientList = new ArrayList<>();
        clientList.add(this);
    }

}
