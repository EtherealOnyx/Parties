package io.sedu.mc.parties.api.thirstmod;

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

import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

public class TMEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 2) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    UUID player;
                    int thirst;
                    boolean update = PlayerData.playerList.get(player = e.player.getUUID()).setThirst(thirst = TMCompatManager.getHandler().getThirst(e.player));
                    if (update)
                       trackers.forEach((id, serverTracked) -> {
                            InfoPacketHelper.sendThirstUpdate(id, player, thirst);
                       });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> {
            InfoPacketHelper.sendThirstUpdate(sendTo, propOf, PlayerData.playerList.get(propOf).getThirst());
        });
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().isPaused()) return;
            if (ClientEvent.tick % 10 == 2) {
                ClientPlayerData.getSelf(ClientPlayerData::updateThirst);
            }
        }
    }
}
