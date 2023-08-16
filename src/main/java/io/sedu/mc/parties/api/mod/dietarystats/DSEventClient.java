package io.sedu.mc.parties.api.mod.dietarystats;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DSEventClient {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (event.getPlayer() != null)
            PlayerAPI.getClientPlayer(event.getPlayer().getUUID(), player -> DSCompatManager.getHandler().getMaxHunger(event.getPlayer(), (max) -> {
                if (max > 0) {
                    Parties.LOGGER.debug("Setting max hunger on client-side.");
                    player.setMaxHunger(max);
                }
            }));
    }
}
