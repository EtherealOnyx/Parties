package io.sedu.mc.parties.api.mod.epicfight;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerUpdateInterval;

public class EFEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerUpdateInterval.get() == 8) {
                EFCompatManager.getHandler().getClientValues(e.player, (cur, max) -> {
                    HashMap<UUID, Boolean> trackers;
                    if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                        UUID player;
                        PlayerData pd;
                        (pd = PlayerData.playerList.get(player = e.player.getUUID())).setStamina(cur, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendStaminaUpdate(id, player, cur)));

                        pd.setMaxStamina(max, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendMaxStaminaUpdate(id, player, max)));
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> {
            PlayerData pD;
            InfoPacketHelper.sendStaminaUpdate(sendTo, propOf, (pD = PlayerData.playerList.get(propOf)).getStamina());
            InfoPacketHelper.sendMaxStaminaUpdate(sendTo, propOf, pD.getMaxStamina());
        });
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (ClientEvent.tick % 5 == 3) {
                ClientPlayerData.getSelf(ClientPlayerData::updateStamEF);
            }

        }
    }
}
