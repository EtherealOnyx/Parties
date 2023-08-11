package io.sedu.mc.parties.api.mod.hardcorerevival;

import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class HRCompatManager {
    private static IHRHandler handler = FakeHandler.INST;
    public static IHRHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("hardcorerevival") && !ModList.get().isLoaded("incapacitated")) initCompat();
    }

    private static void initCompat() {
        handler = new HRHandler();
        MinecraftForge.EVENT_BUS.register(HREventHandler.class);
    }
}
