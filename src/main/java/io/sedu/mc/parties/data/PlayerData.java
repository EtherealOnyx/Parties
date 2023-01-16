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



    //Player Entity
    WeakReference<ServerPlayer> serverPlayer;

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
        return this;
    }

    public PlayerData removeServerPlayer() {
        serverPlayer = null;
        return this;
    }
}
