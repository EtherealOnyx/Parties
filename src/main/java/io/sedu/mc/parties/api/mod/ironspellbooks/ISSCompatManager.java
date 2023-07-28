package io.sedu.mc.parties.api.mod.ironspellbooks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class ISSCompatManager {
    private static IISSHandler handler = new ISSHandlerFake();


    public static IISSHandler getHandler() {
        return handler;
    }

    private static boolean active = false;

    public static void init() {
        if (ModList.get().isLoaded("irons_spellbooks")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        handler = new ISSHandler();
        active = true;
        MinecraftForge.EVENT_BUS.register(ISSEventHandler.class);
    }
}
