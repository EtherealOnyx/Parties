package io.sedu.mc.parties.data;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.server.level.ServerPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    public static HashMap<UUID, PlayerData> playerList = new HashMap<>();

    public static UUID leader;



    //Player Entity
    WeakReference<ServerPlayer> serverPlayer;

    //Client Information
    private static int globalIndex;
    private int partyIndex;
    private String name;

    //The UUID of the party that this player belongs to.
    private UUID party;

    //Client-side functionality.
    private boolean isOnline;

    public PlayerData(UUID id) {
        playerList.put(id, this);
    }

    //Client constructor.
    public PlayerData() {

    }

    public static void updatePartyIndex(int indexRemoved) {
        globalIndex--;
        playerList.values().forEach((playerData) -> {
            if (playerData.partyIndex > indexRemoved)
                playerData.partyIndex--;
        });
    }

    public static int partySize() {
        return globalIndex;
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
        partyIndex = globalIndex;
        globalIndex++;
        playerList.put(uuid, this);
    }

    public PlayerData setServerPlayer(ServerPlayer player) {
        serverPlayer = new WeakReference<>(player);
        return this;
    }

    public PlayerData removeServerPlayer() {
        serverPlayer = null;
        return this;
    }

    public int getIndex() {
        return partyIndex;
    }
}
