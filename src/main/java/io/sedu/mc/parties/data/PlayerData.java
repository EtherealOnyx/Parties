package io.sedu.mc.parties.data;

import io.sedu.mc.parties.events.PartyEvent;
import net.minecraft.server.level.ServerPlayer;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    public static HashMap<UUID, PlayerData> playerList = new HashMap<>();

    //Boolean true = server side tracking, false = client side tracking.
    //Inner UUID belongs to ID that is tracking the outer player.
    public static HashMap<UUID,HashMap<UUID, Boolean>> playerTrackers = new HashMap<>();

    //Left = Tracked, right = trackers
    public static HashMap<UUID, UUID> xpTrackers = new HashMap<>();





    //Player Entity
    protected WeakReference<ServerPlayer> serverPlayer;

    //Player Entity name
    private String name;

    //Invite tracker
    private HashMap<UUID, Short> inviters = new HashMap<>();
    //Player old hunger;
    private int oldHunger;

    //Player old bar;
    private float xpBar;

    //The UUID of the party that this player belongs to.
    private UUID party;

    public PlayerData(UUID id) {
        playerList.put(id, this);
    }

    public boolean hasParty() {
        return party != null;
    }

    public boolean isInviter(UUID inviter) {
        return inviters.containsKey(inviter);
    }

    public void removeInviter(UUID inviter) {
        inviters.remove(inviter);
    }

    public void addInviter(UUID inviter) {
        inviters.put(inviter, PartyEvent.playerAcceptTimer);
    }

    public boolean addParty(UUID id) {
        if (party != null)
            return false;
        party = id;
        return true;
    }

    public ServerPlayer getPlayer() {
        if (serverPlayer != null)
            return serverPlayer.get();
        return null;
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

    public void tickInviters() {
        HashMap<UUID, Short> invNew = new HashMap<>();
        inviters.forEach((uuid, aShort) -> {
            if (aShort-- <= 0) {
                PartyHelper.dismissInvite(this, uuid);
            } else {
                invNew.put(uuid, aShort);
            }
        });
        inviters = invNew;
    }

    public void removeInviters() {
        inviters.forEach((uuid, aShort) -> {
            PartyHelper.dismissInvite(uuid);
        });
        inviters.clear();
    }

    public boolean setXpBar(float v) {
        if (xpBar != v) {
            xpBar = v;
            return true;
        }
        return false;
    }
}
