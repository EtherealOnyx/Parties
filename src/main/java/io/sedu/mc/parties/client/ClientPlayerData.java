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
    private String playerName;
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

    public void setId(UUID uuid) {
        partyIndex = globalIndex;
        globalIndex++;
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

    public String getName() {
        return trackedOnClient ? clientPlayer.getDisplayName().getContents() : playerName;
    }

    public void setClientPlayer(Player entity) {
        clientPlayer = entity;
        trackedOnClient = true;
        playerName = entity.getDisplayName().getContents();
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

    public static String getName(UUID uuid) {
        return playerList.get(uuid).getName();
    }
}
