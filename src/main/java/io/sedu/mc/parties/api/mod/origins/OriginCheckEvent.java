package io.sedu.mc.parties.api.mod.origins;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.events.ClientEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static io.sedu.mc.parties.api.mod.origins.OCompatManager.eventInstance;

public class OriginCheckEvent {

    private OHandler handler;

    public OriginCheckEvent(OHandler handler) {
        this.handler = handler;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && ClientEvent.tick % 40 == 4) {
            if (handler.isPresent()) {
                handler.getOriginList().forEach(origin -> {
                    if (origin.getRegistryName() == null) {
                        Parties.LOGGER.error("An origin doesn't have a valid registry name! Skipping...");
                    } else {
                        new OriginHolder(origin.getRegistryName().toString(), origin.getName().getString(), origin.getDescription().getString(), origin.getIcon());
                    }
                });
                OriginHolder.printOriginInfo();
                //Unregister the tracker.
                MinecraftForge.EVENT_BUS.unregister(eventInstance);
                OHandler.ready = true;
                //Load the origins.
            } else {
                Parties.LOGGER.debug("Origin not loaded yet.");
            }
        }
    }
}
