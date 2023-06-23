package io.sedu.mc.parties.api.mod.origins;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OCommonEventHandler {
    @SubscribeEvent
    public void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> PlayerAPI.getPlayer(propOf, p -> InfoPacketHelper.sendOriginUpdate(sendTo, propOf, PlayerData.playerList.get(propOf).getOrigin())));
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        String o = OCompatManager.getHandler().getMainOrigin(event.getPlayer());
        Parties.LOGGER.debug(o);
        InfoPacketHelper.sendOriginUpdate(event.getPlayer().getUUID(), o);
    }
}
