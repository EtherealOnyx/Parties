package io.sedu.mc.parties.api.mod.toughasnails;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class TANCompatManager {
    private static ITANHandler handler = new TANHandlerFake();
    private static boolean active = false;


    public static ITANHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("toughasnails")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        handler = new TANHandler();
        MinecraftForge.EVENT_BUS.register(TANEventHandler.class);
        active = true;
    }
}
