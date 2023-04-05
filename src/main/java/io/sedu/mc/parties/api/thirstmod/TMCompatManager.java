package io.sedu.mc.parties.api.thirstmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class TMCompatManager {
    private static ITMHandler handler = new TMHandlerFake();


    public static ITMHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("thirst") && !ModList.get().isLoaded("toughasnails")) initCompat();
    }

    private static void initCompat() {
        if (TMHandler.exists()) {
            handler = new TMHandler();
            MinecraftForge.EVENT_BUS.register(TMEventHandler.class);
        }
    }
}
