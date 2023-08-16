package io.sedu.mc.parties.api.mod.gamestages;

import io.sedu.mc.parties.data.ClientConfigData;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import io.sedu.mc.parties.network.StageTypeSync;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GSEventClient {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        if (event.getPlayer() != null)
            PartiesPacketHandler.sendToServer(new StageTypeSync(ClientConfigData.defaultSync.get()));
    }
}
