package io.sedu.mc.parties.api.mod.thirstmod;

import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class TMCompatManager {
    private static ITMHandler handler = FakeHandler.INST;
    private static boolean active = false;
    public static boolean enableOverlay = true;

    public static ITMHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("thirst") && !ModList.get().isLoaded("toughasnails")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        if (TMHandler.exists()) {
            handler = new TMHandler();
            MinecraftForge.EVENT_BUS.register(TMEventHandler.class);
            active = true;
        }
    }
}
