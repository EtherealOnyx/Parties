package io.sedu.mc.parties.api.mod.spellsandshields;

import io.sedu.mc.parties.api.helper.PlayerAPI;
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

import static io.sedu.mc.parties.data.ServerConfigData.playerSlowUpdateInterval;

public class SSEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerSlowUpdateInterval.get() == 8) {
                SSCompatManager.getHandler().getAllMana(e.player, (cur, max, absorb) -> {
                    HashMap<UUID, Boolean> trackers;
                    if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                        UUID player;
                        PlayerData pd;
                        (pd = PlayerData.playerList.get(player = e.player.getUUID())).setManaSS(cur, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendManaUpdateSS(id, player, cur)));
                        pd.setMaxManaSS(max, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendMaxManaUpdateSS(id, player, cur)));
                        pd.setExtraManaSS(absorb, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendExtraManaUpdateSS(id, player, cur)));
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> PlayerAPI.getPlayer(propOf, p -> {
            InfoPacketHelper.sendManaUpdateSS(sendTo, propOf, p.getManaSS());
            InfoPacketHelper.sendMaxManaUpdateSS(sendTo, propOf, p.getMaxManaSS());
            InfoPacketHelper.sendExtraManaUpdateSS(sendTo, propOf, p.getExtraManaSS());
        }));
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (ClientEvent.tick % 20 == 2) {
                ClientPlayerData.getSelf(ClientPlayerData::checkMaxManaSS);
            }

        }
    }
}
