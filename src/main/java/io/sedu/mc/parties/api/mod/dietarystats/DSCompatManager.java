package io.sedu.mc.parties.api.mod.dietarystats;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class DSCompatManager {
    
    private static IDSHandler handler = new DSHandlerFake();
    private static boolean active = false;


    public static IDSHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("dietarystatistics")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        active = true;
        handler = new DSHandler();
        MinecraftForge.EVENT_BUS.register(DSEventHandler.class);
    }
}
