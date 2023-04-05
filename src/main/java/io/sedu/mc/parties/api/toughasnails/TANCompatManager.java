package io.sedu.mc.parties.api.toughasnails;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class TANCompatManager {
    private static ITANHandler handler = new TANHandlerFake();


    public static ITANHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("toughasnails")) initCompat();
    }

    private static void initCompat() {
        handler = new TANHandler();
        MinecraftForge.EVENT_BUS.register(TANEventHandler.class);
    }
}
