package io.sedu.mc.parties.data;

import net.minecraft.server.level.ServerPlayer;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    public static HashMap<UUID, PlayerData> playerList = new HashMap<>();
    //Boolean true = server side tracking, false = client side tracking.
    //Inner UUID belongs to ID that the outer UUID is tracking.
    public static HashMap<UUID, HashMap<UUID, Boolean>> trackerListOld = new HashMap<>();



    //Player Entity
    WeakReference<ServerPlayer> serverPlayer;

    //Player Entity name
    private String name;

    //The UUID of the party that this player belongs to.
    private UUID party;

    public PlayerData(UUID id) {
        playerList.put(id, this);
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

    public void removeParty() {
        party = null;
    }

    public PlayerData setServerPlayer(ServerPlayer player) {
        serverPlayer = new WeakReference<>(player);
        name = player.getName().getContents();
        return this;
    }

    public String getName() {
        return serverPlayer.get() != null ? serverPlayer.get().getName().getContents() : name;
    }

    public PlayerData removeServerPlayer() {
        serverPlayer = null;
        return this;
    }
}
