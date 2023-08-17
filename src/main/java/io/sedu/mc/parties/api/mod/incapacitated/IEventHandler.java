package io.sedu.mc.parties.api.mod.incapacitated;

import com.cartoonishvillain.incapacitated.Incapacitated;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.api.mod.hardcorerevival.HRCompatManager;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

//TODO: Figure out how to properly sync timers.

public class IEventHandler {

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf, propPlayer) -> {
            ICompatManager.getHandler().getCompleteIncapInfo(propPlayer, (count, duration) -> {
                InfoPacketHelper.sendDowned(sendTo, propOf, true, duration);
                InfoPacketHelper.sendHealth(sendTo, propOf, propPlayer.getHealth());
            });
        });
        event.forTrackersAndSelf((sendTo, propOf, propPlayer) -> HRCompatManager.getHandler().getDowned(propPlayer, (isBleeding, duration) -> {
            if (isBleeding) {
                InfoPacketHelper.sendDowned(sendTo, propOf, true, duration);
                InfoPacketHelper.sendHealth(sendTo, propOf, propPlayer.getHealth());
                InfoPacketHelper.sendReviveUpdate(sendTo, propOf, ServerPlayerData.playerList.get(propOf).getReviveProg());
            }
        }));
    }

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 9) {
                    ICompatManager.getHandler().getReviveCount(e.player, count -> {
                           UUID playerId = e.player.getUUID();
                           PlayerAPI.getPlayer(playerId, player ->
                                   player.setReviveProg(((float) Incapacitated.config.REVIVETICKS.get() - count)
                                                                / Incapacitated.config.REVIVETICKS.get(), (data) -> {
                                           //Send to self as well.
                                           InfoPacketHelper.sendReviveUpdate(playerId, data);
                                           HashMap<UUID, Boolean> trackers;
                                           boolean updateTimer = data == 0;
                                           if (updateTimer) {
                                               InfoPacketHelper.sendDowned((ServerPlayer) e.player, true, ICompatManager.getHandler()
                                                                                                                        .getDeathTicks(e.player));
                                               if ((trackers = ServerPlayerData.playerTrackers.get(playerId)) != null) {
                                                   trackers.forEach((id, serverTracked) -> {
                                                       InfoPacketHelper.sendReviveUpdate(id, playerId, data);
                                                       InfoPacketHelper.sendDowned(id, playerId, true, ICompatManager.getHandler().getDeathTicks(e.player));
                                                   });
                                               }
                                           } else {
                                               if ((trackers = ServerPlayerData.playerTrackers.get(playerId)) != null) {
                                                   trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendReviveUpdate(id, playerId, data));
                                               }
                                           }
                                   })
                           );
                    });
                }
        }
    }

    public static void incapacitate(ServerPlayer p, int ticksTillDeath) {
        HashMap<UUID, Boolean> trackers;
        InfoPacketHelper.sendDowned(p, true, ticksTillDeath);
        if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
            trackers.forEach((id, serverTracked) -> {
                InfoPacketHelper.sendDowned(id, p.getUUID(), true, ticksTillDeath + 2);
                if (serverTracked)
                    InfoPacketHelper.sendHealth(id, p.getUUID(), p.getHealth());
            });
        }
        ServerPlayerData.playerList.get(p.getUUID()).setDowned(true);
    }

    public static void wakeUp(ServerPlayer p) {
        HashMap<UUID, Boolean> trackers;
        //Make the timer reset when they wake up...
        InfoPacketHelper.sendDowned(p, false, 0);
        if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
            trackers.forEach((id, serverTracked) -> {
                InfoPacketHelper.sendDowned(id, p.getUUID(), false, 0);
                if (serverTracked)
                    InfoPacketHelper.sendHealth(id, p.getUUID(), p.getHealth());
            });
        }
        ServerPlayerData.playerList.get(p.getUUID()).setDowned(false);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            int deathTicks = ICompatManager.getHandler().getDeathTicks(event.getPlayer());
            if (deathTicks != 0) {
                InfoPacketHelper.sendDowned((ServerPlayer) event.getPlayer(), true, deathTicks + 2);
            }
        }
    }
}
