package io.sedu.mc.parties.api.mod.coldsweat;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerSlowUpdateInterval;
import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

public class CSEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 9) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = ServerPlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    try {
                        UUID player;
                        ServerPlayerData.playerList.get(player = e.player.getUUID()).setBodyTemp(CSCompatManager.getHandler().getBodyTemp(e.player), temp -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendBodyTempUpdate(id, player, temp)));
                    } catch (Throwable t) {
                        CSCompatManager.changeHandler();
                        Parties.LOGGER.error("[Parties] Failed to support Cold Sweat!", t);
                        onEntityTick(e);
                    }


                }
            }
            if (e.player.tickCount % playerSlowUpdateInterval.get() == 9) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = ServerPlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    try {
                        UUID player;
                        //World
                        ServerPlayerData.playerList.get(player = e.player.getUUID()).setWorldTemp(CSCompatManager.getHandler().getWorldTemp(e.player), temp -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendWorldTempUpdate(id, player, temp)));
                    } catch (Throwable t) {
                        CSCompatManager.changeHandler();
                        Parties.LOGGER.error("[Parties] Failed to support Cold Sweat!", t);
                        onEntityTick(e);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> {
            ServerPlayerData p = ServerPlayerData.playerList.get(propOf);
            InfoPacketHelper.sendWorldTempUpdate(sendTo, propOf, p.getWorldTemp());
            InfoPacketHelper.sendBodyTempUpdate(sendTo, propOf,p.getBodyTemp());
        });
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().isPaused()) return;
            if (ClientEvent.tick % 20 == 2) {
                //TODO: Reimplement this for client.
                ClientPlayerData.getSelf(ClientPlayerData::updateTemperatures);
            }
        }
    }
}
