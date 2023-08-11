package io.sedu.mc.parties.api.mod.homeostatic;

import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class HCompatManager {

    private static IHHandler handler = FakeHandler.INST;
    private static boolean active = false;


    public static IHHandler getHandler() {
        return handler;
    }

    public static boolean active() {
        return active;
    }

    public static void init() {
        if (ModList.get().isLoaded("homeostatic")) initCompat();
    }

    private static void initCompat() {
        active = true;
        HHandler newHandler = new HHandler();
        if (newHandler.exists()) {
            handler = newHandler;
            MinecraftForge.EVENT_BUS.register(HEventHandler.class);
        }

    }
}
