package io.sedu.mc.parties.api.playerrevive;

import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.events.PartyJoinEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.event.PlayerRevivedEvent;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

public class PREventHandler {

    @SubscribeEvent
    public static void onPlayerRevived(PlayerRevivedEvent event) {
        HashMap<UUID, Boolean> trackers;
        if (event.getEntity() instanceof Player p) {
            InfoPacketHelper.sendBleeding((ServerPlayer)p, false, 0);
            if ((trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.forEach((id, serverTracked) -> {
                    InfoPacketHelper.sendBleeding(id, p.getUUID(), false, 0);
                    if (serverTracked)
                        InfoPacketHelper.sendHealth(id, p.getUUID(), PlayerRevive.CONFIG.revive.healthAfter);
                 });
            }
            PlayerData.playerList.get(p.getUUID()).setBleeding(false);
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onEntityDeath(LivingDeathEvent event) {
        if (!event.getEntityLiving().level.isClientSide()) {
            if (event.isCanceled()) {
                if (event.getEntity() instanceof Player p) {
                    PRCompatManager.getHandler().getBleed(p, (isBleeding, duration) -> {
                        if (isBleeding) {
                            HashMap<UUID, Boolean> trackers;
                            InfoPacketHelper.sendBleeding((ServerPlayer)p, true, duration);
                            if ((trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                                trackers.forEach((id, serverTracked) -> {
                                    InfoPacketHelper.sendBleeding(id, p.getUUID(), true, duration);
                                    if (serverTracked)
                                        InfoPacketHelper.sendHealth(id, p.getUUID(), p.getHealth());
                                });
                            }
                            PlayerData.playerList.get(p.getUUID()).setBleeding(true);
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf, propPlayer) -> PRCompatManager.getHandler().getBleed(propPlayer, (isBleeding, duration) -> {
            if (isBleeding) {
                InfoPacketHelper.sendBleeding(sendTo, propOf, true, duration);
                InfoPacketHelper.sendHealth(sendTo, propOf, propPlayer.getHealth());
                InfoPacketHelper.sendReviveUpdate(sendTo, propOf, PlayerData.playerList.get(propOf).getReviveProg());
            }
        }));
    }

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 1) {
                if (!PlayerData.playerList.get(e.player.getUUID()).isBleeding()) return;
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    UUID player;
                    float revive;
                    PlayerData.playerList.get(player = e.player.getUUID())
                                         .setReviveProg(revive = PRCompatManager.getHandler().getReviveProgress(e.player),
                                                        () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendReviveUpdate(id, player, revive)));
                }
            }
        }
    }
}
