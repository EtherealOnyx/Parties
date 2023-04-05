package io.sedu.mc.parties.api.toughasnails;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.events.PartyJoinEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerSlowUpdateInterval;
import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

public class TANEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 9) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    UUID player;
                    int thirst;
                    //Thirst
                    boolean update = PlayerData.playerList.get(player = e.player.getUUID()).setThirst(thirst = TANCompatManager.getHandler().getPlayerThirst(e.player));
                    if (update)
                        trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendThirstUpdate(id, player, thirst));
                }
            }
            if (e.player.tickCount % playerSlowUpdateInterval.get() == 9) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    UUID player;
                    int temp;
                    //Temperature
                    boolean update = PlayerData.playerList.get(player = e.player.getUUID()).setWorldTemp(temp = TANCompatManager.getHandler().getPlayerTemp(e.player));
                    if (update)
                        trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendWorldTempUpdateTAN(id, player, temp));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> {
            InfoPacketHelper.sendWorldTempUpdateTAN(sendTo, propOf, PlayerData.playerList.get(propOf).getWorldTemp());
            InfoPacketHelper.sendThirstUpdate(sendTo, propOf, PlayerData.playerList.get(propOf).getThirst());
        });
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().isPaused()) return;
            if (ClientEvent.tick % 40 == 2) {
                ClientPlayerData.playerList.values().forEach(ClientPlayerData::updateTemperaturesTAN);
            }
            if (ClientEvent.tick % 10 == 2) {
                ClientPlayerData.playerList.values().forEach(ClientPlayerData::updateThirstTAN);
            }
        }
    }
}
