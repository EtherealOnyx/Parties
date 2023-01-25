package io.sedu.mc.parties.data;

import net.minecraft.server.level.ServerPlayer;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    public static HashMap<UUID, PlayerData> playerList = new HashMap<>();
    //Boolean true = server side tracking, false = client side tracking.
    //Inner UUID belongs to ID that the outer UUID is tracking.
    public static HashMap<UUID, HashMap<UUID, Boolean>> trackerListO = new HashMap<>();

    //Inner UUID belongs to ID that is tracking the outer player.
    public static HashMap<UUID,HashMap<UUID, Boolean>> playerTrackers = new HashMap<>();





    //Player Entity
    protected WeakReference<ServerPlayer> serverPlayer;

    //Player Entity name
    private String name;

    //Player tick;
    private short tick = 0;
    //Player old hunger;
    private int oldHunger;

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

    public ServerPlayer getPlayer() {
        return serverPlayer.get();
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
        return serverPlayer != null && serverPlayer.get() != null ? serverPlayer.get().getName().getContents() : name;
    }

    public PlayerData removeServerPlayer() {
        serverPlayer = null;
        return this;
    }

    public static void addTracker(UUID trackerHost, UUID toTrack) {
        if(!playerTrackers.containsKey(toTrack)) {
            playerTrackers.put(toTrack, new HashMap<>());
        }
        playerTrackers.get(toTrack).put(trackerHost, true);
    }

    public static void removeTracker(UUID trackerHost, UUID toTrack) {
        if(!playerTrackers.containsKey(toTrack)) {
            return;
        }
        playerTrackers.get(toTrack).remove(trackerHost);
        if (playerTrackers.get(toTrack).size() == 0)
            playerTrackers.remove(toTrack);
    }

    public static void changeTracker(UUID trackerHost, UUID toTrack, boolean serverTracked) {
        playerTrackers.get(toTrack).put(trackerHost, serverTracked);
    }

    public boolean setHunger(int hunger) {
        if (oldHunger != hunger) {
            oldHunger = hunger;
            return true;
        }
        return false;
    }

    public int getHunger() {
        return oldHunger;
    }
}
