package io.sedu.mc.parties.api.mod.feathers;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class FCompatManager {
    public static boolean enableOverlay = true;
    private static IFHandler handler = new FHandlerFake();
    private static boolean active = false;


    public static IFHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("feathers")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        if (FHandler.exists()) {
            active = true;
            handler = new FHandler();
            MinecraftForge.EVENT_BUS.register(FEventHandler.class);
        }

    }
}
