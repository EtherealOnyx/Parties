package io.sedu.mc.parties.api.mod.incapacitated;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class ICompatManager {

    private static IIHandler handler = new IHandlerFake();

    public static IIHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("incapacitated")) initCompat();
    }

    private static void initCompat() {
        handler = new IHandler();
        MinecraftForge.EVENT_BUS.register(IEventHandler.class);
    }
}
