package io.sedu.mc.parties.api.coldsweat;

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

public class CSEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 9) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    try {
                        UUID player;
                        float temp;
                        boolean update;
                        update = PlayerData.playerList.get(player = e.player.getUUID()).setBodyTemp(temp = CSCompatManager.getHandler().getBodyTemp(e.player));
                        //Body
                        if (update)
                            trackers.forEach((id, serverTracked) -> {
                                if (serverTracked)
                                    InfoPacketHelper.sendBodyTempUpdate(id, player, temp);
                            });
                    } catch (Throwable t) {
                        CSCompatManager.changeHandler();
                        onEntityTick(e);
                    }


                }
            }
            if (e.player.tickCount % playerSlowUpdateInterval.get() == 9) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    try {
                        UUID player;
                        float temp;
                        //World
                        boolean update = PlayerData.playerList.get(player = e.player.getUUID()).setWorldTemp(temp = CSCompatManager.getHandler()
                                                                                                                                   .getWorldTemp(e.player));
                        if (update)
                            trackers.forEach((id, serverTracked) -> {
                                if (serverTracked)
                                    InfoPacketHelper.sendWorldTempUpdate(id, player, temp);
                            });
                    } catch (Throwable t) {
                        CSCompatManager.changeHandler();
                        onEntityTick(e);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> {
            PlayerData p = PlayerData.playerList.get(propOf);
            InfoPacketHelper.sendWorldTempUpdate(sendTo, propOf, p.getWorldTemp());
            InfoPacketHelper.sendBodyTempUpdate(sendTo, propOf,p.getBodyTemp());
        });
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().isPaused()) return;
            if (ClientEvent.tick % 20 == 2) {
                ClientPlayerData.playerList.values().forEach(ClientPlayerData::updateTemperatures);
            }
        }
    }
}
