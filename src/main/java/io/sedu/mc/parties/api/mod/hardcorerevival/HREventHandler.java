package io.sedu.mc.parties.api.mod.hardcorerevival;

import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.blay09.mods.hardcorerevival.api.PlayerKnockedOutEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

public class HREventHandler {

    @SubscribeEvent
    public static void playerKnockedOut(PlayerKnockedOutEvent event) {
        Player p;
        HRCompatManager.getHandler().getDowned(p = event.getPlayer(), (isDowned, duration) -> {
            if (isDowned) {
                HashMap<UUID, Boolean> trackers;
                InfoPacketHelper.sendDowned((ServerPlayer)p, true, duration);
                if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                    trackers.forEach((id, serverTracked) -> {
                        InfoPacketHelper.sendDowned(id, p.getUUID(), true, duration);
                        if (serverTracked)
                            InfoPacketHelper.sendHealth(id, p.getUUID(), p.getHealth());
                    });
                }
                ServerPlayerData.playerList.get(p.getUUID()).setDowned(true);
            }
        });
    }

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 3) {
                AtomicReference<HashMap<UUID, Boolean>> trackers = new AtomicReference<>();
                HRCompatManager.getHandler().getReviveProgress(e.player, (revive, targetPlayer) -> {
                    InfoPacketHelper.sendReviveUpdate(targetPlayer.getUUID(), revive);
                    trackers.set(ServerPlayerData.playerTrackers.get(targetPlayer.getUUID()));
                    if (trackers.get() != null) {
                        UUID player;
                        ServerPlayerData.playerList.get(player = targetPlayer.getUUID()).setReviveProg(revive, (data) -> trackers.get().forEach((id, serverTracked) -> InfoPacketHelper.sendReviveUpdate(id, player, revive)));
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf, propPlayer) -> HRCompatManager.getHandler().getDowned(propPlayer, (isBleeding, duration) -> {
            if (isBleeding) {
                InfoPacketHelper.sendDowned(sendTo, propOf, true, duration);
                InfoPacketHelper.sendHealth(sendTo, propOf, propPlayer.getHealth());
                InfoPacketHelper.sendReviveUpdate(sendTo, propOf, ServerPlayerData.playerList.get(propOf).getReviveProg());
            }
        }));
    }

    public static void sendWakeUpEffect(Player p) {
        HashMap<UUID, Boolean> trackers;
        //Make the timer reset when they wake up...
        InfoPacketHelper.sendDowned((ServerPlayer)p, false, 0);
        if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
            trackers.forEach((id, serverTracked) -> {
                InfoPacketHelper.sendDowned(id, p.getUUID(), false, 0);
                if (serverTracked)
                    InfoPacketHelper.sendHealth(id, p.getUUID(), p.getHealth());
            });
        }
        ServerPlayerData.playerList.get(p.getUUID()).setDowned(false);
    }

    public static void abortRescue(Player rescueTarget) {
        if (rescueTarget != null) {
            InfoPacketHelper.sendReviveUpdate(rescueTarget.getUUID(), 0);
            HashMap<UUID, Boolean> trackers = ServerPlayerData.playerTrackers.get(rescueTarget.getUUID());
            if (trackers != null) {
                UUID player;
                ServerPlayerData.playerList.get(player = rescueTarget.getUUID()).setReviveProg(0, (data) -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendReviveUpdate(id, player, 0)));
            }
        }
    }
}
