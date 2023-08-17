package io.sedu.mc.parties.api.mod.gamestages;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PartyAPI;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.ServerConfigData;
import io.sedu.mc.parties.data.ServerPlayerData;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
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
        //Send message notifying them of the stage syncing.
        SyncType servSync = ServerConfigData.syncGameStages.get();
        event.getPlayer().sendMessage(
                new TranslatableComponent("messages.sedparties.phandler.gamestagesync").withStyle(ChatFormatting.DARK_AQUA)
                                                                                       .append(new TranslatableComponent("messages.sedparties.phandler.sync-none").withStyle(

                                                                                               style -> style.withColor(ChatFormatting.GREEN)
                                                                                                             .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                                                                                            "/party sync NONE"))
                                                                                                             .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                                                                                            new TranslatableComponent("messages.sedparties.phandler.sync-none2"))))
                                                                                       ).append(" ")
                                                                                       .append(new TranslatableComponent("messages.sedparties.phandler.sync-future").withStyle(

                                                                                               style -> style.withColor(servSync != NONE ? ChatFormatting.GREEN : ChatFormatting.GRAY)
                                                                                                             .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                                                                                            "/party sync FUTURE"))
                                                                                                             .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                                                                                            new TranslatableComponent("messages.sedparties.phandler.sync-future" + (servSync != NONE ? "2" : "3")))))
                                                                                       ).append(" ")
                                                                                       .append(new TranslatableComponent("messages.sedparties.phandler.sync-all").withStyle(

                                                                                               style -> style.withColor(servSync == ALL ? ChatFormatting.GREEN : ChatFormatting.GRAY)
                                                                                                             .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                                                                                            "/party sync ALL"))
                                                                                                             .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                                                                                            new TranslatableComponent("messages.sedparties.phandler.sync-all" + (servSync == ALL ? "2" : "3")))))
                                                                                       ).append(" ")

                , event.getPlayer().getUUID());
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
        //Prevent party stage sync if server setting isn't set to ALL.
        Parties.LOGGER.debug("Sync ALL check");
        if (ServerConfigData.syncGameStages.get() != SyncType.ALL) return;
        Parties.LOGGER.debug("Party Recalculation");
        recalculatePartyStages(partyId);
    }

    private static void checkPartyStages(UUID partyId) {
        partyStages.computeIfAbsent(partyId, k -> new HashSet<>());
    }

    private static void recalculatePartyStages(UUID partyId) {
        HashSet<String> newStages = new HashSet<>();
        PartyData p = PartyAPI.getPartyFromId(partyId);
        if (p == null) {
            Parties.LOGGER.error("[Parties] Error initializing game stage pool for party. This might break your game!");
        } else {
            Parties.LOGGER.debug("Attempting to add stages from each member...");
            p.getMembers().forEach(member -> PlayerAPI.getPlayer(member, playerData -> {
                if (playerData.getGSyncType() == ALL) {
                    Parties.LOGGER.debug("Attempting to add stages from {}...", playerData.getName());
                    IStageData data = GameStageSaveHandler.getPlayerData(member);
                    if (data != null) {
                        newStages.addAll(data.getStages());
                        Parties.LOGGER.debug("Potentially added {} stages.", data.getStages().size());
                    }

                }
            }));
            checkPartyStages(partyId);
            partyStages.put(partyId, newStages);
            //Party stage sync for everyone that opted in
            PartyAPI.getPartyFromId(partyId).getMembers().forEach(memberId -> PlayerAPI.getPlayer(memberId, (serverPlayerData -> {
                if (serverPlayerData.getGSyncType() == SyncType.ALL) {
                    final IStageData data = GameStageSaveHandler.getPlayerData(memberId);
                    if (data != null) {
                        checkPartyStages(partyId);
                        Parties.LOGGER.debug("Adding {} stages for {}.", partyStages.get(partyId).size(), serverPlayerData.getName());
                        partyStages.get(partyId).forEach(data::addStage);
                    }
                }
            })));
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
    public static void onStageAdded(GameStageEvent.Added event) {
        //Check server party settings.
        if (ServerConfigData.syncGameStages.get() == NONE) return;
        //Check player's sync settings.
        PlayerAPI.getPlayer(event.getPlayer().getUUID(), player -> {
            if (player.getGSyncType() != NONE) {
                Parties.LOGGER.debug("Player {}'s sync type is set to {}. Sharing stage...", event.getPlayer().getScoreboardName(), player.getGSyncType());
                //Sync's all & future entries.
                PartyAPI.getOnlineMembersWithoutSelf(event.getPlayer().getUUID()).forEach(member -> {

                    PlayerAPI.getPlayer(member.getUUID(), serverPlayerData -> {
                        //Add stage only if player's setting is not NONE.
                        if (serverPlayerData.getGSyncType() != NONE && !GameStageHelper.hasStage(member, event.getStageName())) {
                            Parties.LOGGER.debug("Player {}'s sync type is set to {}. Retrieving stage...", member.getScoreboardName(), serverPlayerData.getGSyncType());
                            GameStageHelper.addStage((ServerPlayer) member, event.getStageName());
                        }
                    });

                });
                checkPartyStages(player.getPartyId());
                //Add stage only if
                if (player.getGSyncType() == ALL && ServerConfigData.syncGameStages.get() == ALL) {
                    partyStages.get(player.getPartyId()).add(event.getStageName());
                }
            }
        });

    }

    public static boolean changeServerOption(SyncType t) {
        if (ServerConfigData.syncGameStages.get() != t) {
            ServerConfigData.syncGameStages.set(t);
            PartyData.partyList.keySet().forEach(GSEventHandler::recalculatePartyStages);
            return true;
        }
        return false;
    }
}
