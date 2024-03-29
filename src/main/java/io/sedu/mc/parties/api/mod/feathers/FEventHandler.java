package io.sedu.mc.parties.api.mod.feathers;

import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

public class FEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 2) {
                FCompatManager.getHandler().getServerFeathers((ServerPlayer) e.player, (cur, max, abs) -> {
                    HashMap<UUID, Boolean> trackers;
                    if ((trackers = ServerPlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                        UUID player;
                        ServerPlayerData pd;
                        (pd = ServerPlayerData.playerList.get(player = e.player.getUUID())).setStamina(Math.min(cur, max), () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendStaminaUpdate(id, player, cur)));
                        pd.setMaxStamina(max, () ->  trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendMaxStaminaUpdate(id, player, max)));
                        pd.setExtraStamina(abs, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendExtraStamUpdate(id, player, abs)));
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> PlayerAPI.getPlayer(propOf, p -> {
            ServerPlayerData pD;
            InfoPacketHelper.sendStaminaUpdate(sendTo, propOf, (pD = ServerPlayerData.playerList.get(propOf)).getStamina());
            InfoPacketHelper.sendMaxStaminaUpdate(sendTo, propOf, pD.getMaxStamina());
            InfoPacketHelper.sendExtraStamUpdate(sendTo, propOf, pD.getExtraStamina());
        }));
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (ClientEvent.tick % 10 == 2) {
                ClientPlayerData.getSelf(ClientPlayerData::checkStamF);
            }

        }
    }
}
