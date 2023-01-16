package io.sedu.mc.parties.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.ClientPlayerData;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Parties.MODID)
public class ClientEvent {

    /*@SubscribeEvent
    public static void onClientLeave(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientPlayerData.playerList.clear();
    }*/
}
