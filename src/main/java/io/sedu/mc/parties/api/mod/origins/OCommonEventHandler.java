package io.sedu.mc.parties.api.mod.origins;

import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class OCommonEventHandler {
    @SubscribeEvent
    public void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> PlayerAPI.getPlayer(propOf, p -> InfoPacketHelper.sendOriginUpdate(sendTo, propOf, PlayerData.playerList.get(propOf).getOrigin())));
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        String o = OCompatManager.getHandler().getMainOrigin(event.getPlayer());
        InfoPacketHelper.sendOriginUpdate(event.getPlayer().getUUID(), o);
    }

    public static void changeOrigin(UUID propOf, String origin) {
        if (PlayerData.playerList.get(propOf) == null) return;
        PlayerData.playerList.get(propOf).setOrigin(origin, () -> {
            InfoPacketHelper.sendOriginUpdate(propOf, origin);
            HashMap<UUID, Boolean> trackers;
            if ((trackers = PlayerData.playerTrackers.get(propOf)) != null) {
                trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendOriginUpdate(id, propOf, origin));
            }
        });

    }
}
