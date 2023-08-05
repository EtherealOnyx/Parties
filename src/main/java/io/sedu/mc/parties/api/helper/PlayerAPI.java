package io.sedu.mc.parties.api.helper;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.ServerPlayerData;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerAPI {

    public static void getServerPlayer(UUID id, Consumer<ServerPlayer> action) {
        getPlayer(id, (playerData) -> {
            if (playerData.getPlayer() != null)
                action.accept(playerData.getPlayer());
        });
    }

    public static ServerPlayer getNormalServerPlayer(UUID id) {
        ServerPlayerData p;
        return (p = getNormalPlayer(id)) != null ? p.getPlayer() : null;
    }

    public static void getPlayer(UUID id, Consumer<ServerPlayerData> action) {
        ServerPlayerData.playerList.computeIfPresent(id, (uuid, playerData) -> {
            action.accept(playerData);
            return playerData;
        });
    }

    public static @Nullable ServerPlayerData getNormalPlayer(UUID id) {
        return ServerPlayerData.playerList.get(id);
    }

    public static @Nullable ServerPlayerData getPlayerFromId(int id) {
        for (ServerPlayerData serverPlayerData : ServerPlayerData.playerList.values()) {
            if (serverPlayerData.getPlayer() != null && serverPlayerData.getPlayer().getId() == id)
                return serverPlayerData;
        }
        return null;
    }

    public static String getName(UUID id) {
        ServerPlayerData p;
        return (p = getNormalPlayer(id)) != null ? p.getName() : "";
    }

    public static boolean isOnline(UUID id) {
        return getNormalServerPlayer(id) != null;
    }

    /*
    Client-Side Functions
     */

    public static void getClientPlayer(UUID player, Consumer<ClientPlayerData> action) {
        ClientPlayerData p;
        if ((p = ClientPlayerData.playerList.get(player)) != null) {
            action.accept(p);
        }

    }
}
