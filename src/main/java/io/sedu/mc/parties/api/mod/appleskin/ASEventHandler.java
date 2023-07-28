package io.sedu.mc.parties.api.mod.appleskin;

import io.sedu.mc.parties.data.ClientConfigData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import squeek.appleskin.api.event.HUDOverlayEvent;

public class ASEventHandler {

    //Hunger events
    @SubscribeEvent
    public static void onRenderExhaustion(HUDOverlayEvent.Exhaustion event) {
        hungerCheck(event);
    }

    @SubscribeEvent
    public static void onRenderSaturation(HUDOverlayEvent.Saturation event) {
        hungerCheck(event);
    }

    @SubscribeEvent
    public static void onRenderHunger(HUDOverlayEvent.HungerRestored event) {
        hungerCheck(event);
    }

    private static void hungerCheck(HUDOverlayEvent event) {
        if (!ClientConfigData.renderHunger.get()) event.setCanceled(true);
    }

    //Health events
    @SubscribeEvent
    public static void onRenderHealth(HUDOverlayEvent.HealthRestored event) {
        if (!ClientConfigData.renderPlayerHealth.get()) {
            event.setCanceled(true);
        }
    }

}
