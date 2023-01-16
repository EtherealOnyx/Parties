package io.sedu.mc.parties.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    public static HashMap<UUID, PlayerData> playerList = new HashMap<>();
    //Boolean true = server side tracking, false = client side tracking.
    //Inner UUID belongs to ID that the outer UUID is tracking.
    public static HashMap<UUID, HashMap<UUID, Boolean>> trackerList = new HashMap<>();

    public static HashMap<UUID, Player> potentialTracks = new HashMap<>();



    //Player Entity
    WeakReference<ServerPlayer> serverPlayer;

    //Client Information
    private static int globalIndex;
    private int partyIndex;
    private Player clientPlayer;
    //Client-side functionality.
    private boolean isOnline;
    private Component playerName;
    private boolean trackedOnClient;
    public static UUID leader;

    //The UUID of the party that this player belongs to.
    private UUID party;

    public PlayerData(UUID id) {
        playerList.put(id, this);
    }

    //Client constructor.
    public PlayerData() {
        trackedOnClient = false;
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

    public static String getName(UUID uuid) {
        return PlayerData.playerList.get(uuid).getName().getContents();
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

        for (PlayerInfo pi : Minecraft.getInstance().player.connection.getOnlinePlayers()) {
            if (pi.getProfile().getId().equals(uuid)) {
                playerName = new TextComponent(pi.getProfile().getName());
            }
        }

        playerName = playerName == null ? new TextComponent("???") : playerName;
        trackedOnClient = false;
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

    public Component getName() {
        return (clientPlayer != null) ? clientPlayer.getDisplayName() : playerName;
    }

    public void setClientPlayer(Player entity) {
        clientPlayer = entity;
        trackedOnClient = true;
        playerName = entity.getDisplayName();
    }

    public void removeClientPlayer() {
        clientPlayer = null;
        trackedOnClient = false;
    }

    public boolean isTrackedOnServer() {
        return !trackedOnClient;
    }

    public Player getClientPlayer() {
        return clientPlayer;
    }
}
