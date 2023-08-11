package io.sedu.mc.parties.api.helper;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.ServerPlayerData;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerAPI {

    /**
     * Attempts to grab the ServerPlayer object of the provided player UUID.
     * @param id The given player UUID to search for.
     * @param action A lambda consumer that provides the ServerPlayer object only if it exists (not null).
     */
    public static void getServerPlayer(UUID id, Consumer<ServerPlayer> action) {
        getPlayer(id, (playerData) -> {
            if (playerData.getPlayer() != null)
                action.accept(playerData.getPlayer());
        });
    }

    /**
     * Attempts to grab the ServerPlayer object of the provided player UUID.
     * @param id The given player UUID to search for.
     * @return A ServerPlayer object that can be null if the ServerPlayer is not cached yet, or offline.
     */
    public static @Nullable ServerPlayer getNormalServerPlayer(UUID id) {
        ServerPlayerData p;
        return (p = getNormalPlayer(id)) != null ? p.getPlayer() : null;
    }

    /**
     * Attempts to grab the ServerPlayerData object of the provided player UUID. Extra player information involving values to keep track of are stored here.
     * @param id The given player UUID to search for.
     * @param action A lambda consumer that provides the ServerPlayerData object only if it exists (not null).
     */
    public static void getPlayer(UUID id, Consumer<ServerPlayerData> action) {
        ServerPlayerData.playerList.computeIfPresent(id, (uuid, playerData) -> {
            action.accept(playerData);
            return playerData;
        });
    }

    /**
     * Attempts to grab the ServerPlayerData object of the provided player UUID. Extra player information involving values to keep track of are stored here.
     * @param id The given player UUID to search for.
     * @return A ServerPlayerData object that can be null if the player is not cached yet.
     */
    public static @Nullable ServerPlayerData getNormalPlayer(UUID id) {
        return ServerPlayerData.playerList.get(id);
    }

    /**
     * Attempts to grab the ServerPlayerData object of the provided entity ID. Extra player information involving values to keep track of are stored here.
     * @param id The given player entity ID to search for.
     * @return A ServerPlayerData object that can be null if the player is not cached yet, or the provided entity id is invalid.
     */
    public static @Nullable ServerPlayerData getPlayerFromId(int id) {
        for (ServerPlayerData serverPlayerData : ServerPlayerData.playerList.values()) {
            if (serverPlayerData.getPlayer() != null && serverPlayerData.getPlayer().getId() == id)
                return serverPlayerData;
        }
        return null;
    }

    /**
     * Attempts to provide a player's cached name.
     * @param id The UUID of the player to check for.
     * @return A string that contains the player's name. This also returns an empty string if the player isn't cached.
     */
    public static String getName(UUID id) {
        ServerPlayerData p;
        return (p = getNormalPlayer(id)) != null ? p.getName() : "";
    }

    /**
     * Checks if the provided player is online or not by checking if the player has been cached. It could be false if the player just logged in and is not cached yet, ensuring that a ServerPlayerData or cached ServerPlayer object would be initialized and valid.
     * @param id The UUID of the player to check for.
     * @return A boolean indicating if the player is online and cached.
     */
    public static boolean isOnline(UUID id) {
        return getNormalServerPlayer(id) != null;
    }

    /*
    Client-Side Functions
     */

    /**
     * Attempts to grab the ClientPlayerData object of the provided player UUID. ClientPlayerData stores rendered information of the client player and other party members.
     * @param playerId The given player UUID to search for.
     * @param action A lambda consumer that provides the ClientPlayerData object only if it exists (not null).
     */
    public static void getClientPlayer(UUID playerId, Consumer<ClientPlayerData> action) {
        ClientPlayerData p;
        if ((p = ClientPlayerData.playerList.get(playerId)) != null) {
            action.accept(p);
        }

    }
}
