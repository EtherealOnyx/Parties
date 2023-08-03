package io.sedu.mc.parties.api.mod.gamestages;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PartyAPI;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.ServerPlayerData;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class GSEventHandler {
    private static HashMap<UUID, HashSet<String>> partyStages = new HashMap<>();
    private static HashMap<UUID, ServerSyncType> playerChoices = new HashMap<>();

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        UUID playerId = event.getPlayer().getUUID();
        if (playerChoices.containsKey(playerId)) {
            updatePartyStages(playerId, Objects.requireNonNull(PlayerAPI.getNormalPlayer(playerId)).getPartyId());
        } else {

        }
    }

    //TODO: Call this method when a player updates their preferences.
    public static void changePlayerOption(UUID playerId, ServerSyncType type) {
        ServerPlayerData pD = PlayerAPI.getNormalPlayer(playerId);
        if (pD != null && pD.hasParty()) {
            playerChoices.put(playerId, type);
            updatePartyStages(playerId, pD.getPartyId());
        }
    }


    private static void updatePartyStages(UUID playerId, UUID partyId) {
        recalculatePartyStages(partyId);
        if (Objects.requireNonNull(playerChoices.getOrDefault(playerId, ServerSyncType.NONE)) == ServerSyncType.ALL) {

        }
    }

    private static void recalculatePartyStages(UUID party) {
        HashSet<String> newStages = new HashSet<>();
        PartyData p = PartyAPI.getPartyFromId(party);
        if (p == null) {
            Parties.LOGGER.error("[Parties] Error initializing game stage pool for party. This might break your game!");
        } else {
            p.getMembers().forEach(member -> {
                if (playerChoices.getOrDefault(member, ServerSyncType.NONE) == ServerSyncType.ALL) {
                    //Get all unlocked game stages by player
                    IStageData data = GameStageSaveHandler.getPlayerData(member);
                    if (data != null) newStages.addAll(data.getStages());
                }
            });
            partyStages.put(party, newStages);
        }
    }
}
