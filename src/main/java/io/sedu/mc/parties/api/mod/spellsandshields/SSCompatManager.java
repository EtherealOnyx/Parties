package io.sedu.mc.parties.api.mod.spellsandshields;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class SSCompatManager {
    public static boolean enableOverlay = true;
    private static ISSHandler handler = new SSHandlerFake();
    private static boolean active = false;


    public static ISSHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("spells_and_shields")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        if (SSHandler.exists()) {
            active = true;
            handler = new SSHandler();
            MinecraftForge.EVENT_BUS.register(SSEventHandler.class);
        }

    }
}
