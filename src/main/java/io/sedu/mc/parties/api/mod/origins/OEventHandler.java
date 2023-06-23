package io.sedu.mc.parties.api.mod.origins;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static io.sedu.mc.parties.api.mod.origins.OCompatManager.eventInstance;

public class OEventHandler {

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        OHandler.ready = false;
        if (eventInstance != null) {
            MinecraftForge.EVENT_BUS.register(eventInstance);
        } else {
            Parties.LOGGER.error("Error properly loading Origins Support on the client!");
        }
    }


}
