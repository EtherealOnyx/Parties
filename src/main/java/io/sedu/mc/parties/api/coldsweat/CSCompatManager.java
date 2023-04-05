package io.sedu.mc.parties.api.coldsweat;

import dev.momostudios.coldsweat.config.ColdSweatConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class CSCompatManager {
    private static ICSHandler handler = new CSHandlerFake();


    public static ICSHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("cold_sweat") && !ModList.get().isLoaded("toughasnails")) initCompat();
    }

    private static void initCompat() {
        ICSHandler newHandler = new CSHandler();
        if (newHandler.exists()) {
            handler = newHandler;
            MinecraftForge.EVENT_BUS.register(CSEventHandler.class);
            ColdSweatConfig.getInstance().setRequireThermometer(false);
        }

    }
}
