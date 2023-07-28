package io.sedu.mc.parties.api.mod.homeostatic;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

public class HEventHandler {

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().isPaused()) return;
            if (ClientEvent.tick % 10 == 3) {
                ClientPlayerData.getSelf(ClientPlayerData::updateTemperaturesH);
                ClientPlayerData.getSelf(ClientPlayerData::updateThirstH);
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> {
            ServerPlayerData p = ServerPlayerData.playerList.get(propOf);
            InfoPacketHelper.sendWorldTempUpdate(sendTo, propOf, p.getWorldTemp());
            InfoPacketHelper.sendBodyTempUpdate(sendTo, propOf,p.getBodyTemp());
            InfoPacketHelper.sendThirstUpdate(sendTo, propOf, p.getThirst());
        });
    }

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 9) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = ServerPlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    HCompatManager.getHandler().getTemperature(e.player, (localTemp, skinTemp) -> {
                        UUID player;
                        ServerPlayerData d;
                        (d = ServerPlayerData.playerList.get(player = e.player.getUUID())).setBodyTemp(skinTemp, temp -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendBodyTempUpdate(id, player, temp)));
                        d.setWorldTemp(localTemp, temp -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendWorldTempUpdate(id, player, temp)));
                    });
                    ServerPlayerData.playerList.get(e.player.getUUID()).setThirst(HCompatManager.getHandler().getWaterLevel(e.player), thirst -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendThirstUpdate(id, e.player.getUUID(), thirst)));
                }
            }
        }
    }
}
