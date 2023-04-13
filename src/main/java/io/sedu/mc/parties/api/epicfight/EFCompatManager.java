package io.sedu.mc.parties.api.epicfight;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class EFCompatManager {
    private static IEFHandler handler = new EFHandlerFake();
    private static boolean active = false;


    public static IEFHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("epicfight")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        if (EFHandler.exists()) {
            active = true;
            handler = new EFHandler();
            MinecraftForge.EVENT_BUS.register(EFEventHandler.class);
        }

    }
}
