package io.sedu.mc.parties.api.mod.origins;

import io.sedu.mc.parties.events.ClientEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static io.sedu.mc.parties.api.mod.origins.OCompatManager.eventInstance;

public class OEventHandler {

    //TODO: Put in client event class so the server wont crash.
    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        OHandler.ready = false;
        MinecraftForge.EVENT_BUS.register(eventInstance);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (ClientEvent.tick % 100 == 0 && Minecraft.getInstance().player != null) {
            //Parties.LOGGER.debug(getHandler().getMainOriginClient(Minecraft.getInstance().player));
            OriginHolder.printOriginInfo();
        }
    }




}
