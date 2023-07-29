package io.sedu.mc.parties.api.mod.incapacitated;

import io.sedu.mc.parties.api.mod.homeostatic.HHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class ICompatManager {

    public static void init() {
        if (ModList.get().isLoaded("incapacitated")) initCompat();
    }

    private static void initCompat() {
        HHandler newHandler = new HHandler();
        if (newHandler.exists()) {
            //handler = newHandler;
            MinecraftForge.EVENT_BUS.register(IEventHandler.class);
        }

    }
}
