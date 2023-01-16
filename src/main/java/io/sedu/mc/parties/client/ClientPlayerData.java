package io.sedu.mc.parties.client;

import io.sedu.mc.parties.data.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class ClientPlayerData {
    public static HashMap<UUID, ClientPlayerData> playerList = new HashMap<>();
    public static HashMap<UUID, Player> potentialTracks = new HashMap<>();

    //Client Information
    private static int globalIndex;
    private int partyIndex;
    private Player clientPlayer;
    //Client-side functionality.
    private boolean isOnline;
    private Component playerName;
    private boolean trackedOnClient;
    public static UUID leader;

    //Client constructor.
    public ClientPlayerData() {
        trackedOnClient = false;
    }

    public static void updatePartyIndex(int indexRemoved) {
        globalIndex--;
        playerList.values().forEach((playerData) -> {
            if (playerData.partyIndex > indexRemoved)
                playerData.partyIndex--;
        });
    }

    public static void addClientMember(UUID uuid) {
        ClientPlayerData p = new ClientPlayerData();
        p.setId(uuid);
    }

    public static int partySize() {
        return globalIndex;
    }

    public static String getName(UUID uuid) {
        return playerList.get(uuid).getName().getContents();
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

    public void setOnline() {
        isOnline = true;
    }

    public void setOffline() {
        isOnline = false;
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
