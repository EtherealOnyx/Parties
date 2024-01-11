package io.sedu.mc.parties.api.mod.tfc;

import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class TFCCompatManager {
    private static ITFCHandler handler = FakeHandler.INST;
    private static boolean active = false;


    public static ITFCHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("tfc") && !ModList.get().isLoaded("toughasnails") && !ModList.get().isLoaded("cold_sweat")) initCompat();
    }

    private static void initCompat() {
        handler = new TFCHandler();
        MinecraftForge.EVENT_BUS.register(TFCEventHandler.class);
        active = true;

    }

    public static boolean active() {
        return active;
    }
}
