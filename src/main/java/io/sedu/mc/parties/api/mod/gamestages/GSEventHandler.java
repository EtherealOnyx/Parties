package io.sedu.mc.parties.api.mod.gamestages;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PartyAPI;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.data.ClientConfigData;
import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.ServerConfigData;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import io.sedu.mc.parties.network.StageTypeSync;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static io.sedu.mc.parties.api.mod.gamestages.SyncType.ALL;
import static io.sedu.mc.parties.api.mod.gamestages.SyncType.NONE;
import static net.darkhax.gamestages.GameStageHelper.syncPlayer;

public class GSEventHandler {
    private static final HashMap<UUID, HashSet<String>> partyStages = new HashMap<>();

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        updatePartyStages(event.getPlayer().getUUID(), event.getPartyId());
        syncPlayer((ServerPlayer) event.getPlayer());
    }

    public static boolean changePlayerOption(UUID playerId, SyncType type, boolean forceUpdate) {
        ServerPlayerData pD = PlayerAPI.getNormalPlayer(playerId);
        //Update party stages only if the player exists, has a party, and the sync value changed.
        if (pD != null && pD.hasParty() && (pD.setGSyncType(type) || forceUpdate)) {
            updatePartyStages(playerId, pD.getPartyId(), pD.getGSyncType());
            return true;
        }
        return false;
    }

    private static void updatePartyStages(UUID playerId, UUID partyId) {
        PlayerAPI.getPlayer(playerId, player -> updatePartyStages(playerId, partyId, player.getGSyncType()));
    }
    private static void updatePartyStages(UUID playerId, UUID partyId, SyncType type) {
        recalculatePartyStages(partyId);
        if (type == ALL) {
            final IStageData data = GameStageSaveHandler.getPlayerData(playerId);
            if (data != null) {
                checkPartyStages(partyId);
                partyStages.get(partyId).forEach(data::addStage);
            }
        }
    }

    private static void checkPartyStages(UUID partyId) {
        partyStages.computeIfAbsent(partyId, k -> new HashSet<>());
    }

    private static void recalculatePartyStages(UUID party) {
        HashSet<String> newStages = new HashSet<>();
        PartyData p = PartyAPI.getPartyFromId(party);
        if (p == null) {
            Parties.LOGGER.error("[Parties] Error initializing game stage pool for party. This might break your game!");
        } else {
            p.getMembers().forEach(member -> PlayerAPI.getPlayer(member, playerData -> {
                if (playerData.getGSyncType() == ALL) {
                    IStageData data = GameStageSaveHandler.getPlayerData(member);
                    if (data != null) newStages.addAll(data.getStages());
                }
            }));
            checkPartyStages(party);
            partyStages.put(party, newStages);
        }
    }

    public static boolean validType(SyncType type) {
        SyncType serverType = ServerConfigData.syncGameStages.get();
        return switch(type) {
            case ALL -> serverType == ALL;
            case FUTURE -> serverType != NONE;
            case NONE -> true;
        };
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (event.getPlayer() != null)
            PartiesPacketHandler.sendToServer(new StageTypeSync(ClientConfigData.defaultSync.get()));
    }



    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onStageAdded(GameStageEvent.Added event) {
        //Check server party settings.
        if (ServerConfigData.syncGameStages.get() == NONE) return;
        System.out.println("PASS 1");
        //Check player's sync settings.
        PlayerAPI.getPlayer(event.getPlayer().getUUID(), player -> {
            System.out.println("PASS 2: " + player.getGSyncType());
            if (player.getGSyncType() != NONE) {
                //Sync's all & future entries.
                System.out.println("PASS 3");
                PartyAPI.getOnlineMembersWithoutSelf(event.getPlayer().getUUID()).forEach(member -> {
                    System.out.println("PASS 4M1");
                    if (!GameStageHelper.hasStage(member, event.getStageName())) {
                        System.out.println("PASS 4M2");
                        GameStageHelper.addStage((ServerPlayer) member, event.getStageName());
                        System.out.println("PASS 4M3");
                    }
                });
                checkPartyStages(player.getPartyId());
                //Add stage only if
                if (player.getGSyncType() == ALL && ServerConfigData.syncGameStages.get() == ALL) {
                    System.out.println("PASS 5");
                    partyStages.get(player.getPartyId()).add(event.getStageName());
                }
            }
        });

    }
}
