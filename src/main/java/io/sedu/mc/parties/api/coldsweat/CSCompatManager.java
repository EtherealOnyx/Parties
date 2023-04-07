package io.sedu.mc.parties.api.coldsweat;

import dev.momostudios.coldsweat.config.ColdSweatConfig;
import io.sedu.mc.parties.Parties;
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

    public static void disableSupport() {
        handler = new CSHandlerFake();
        MinecraftForge.EVENT_BUS.unregister(CSEventHandler.class);
    }

    public static void changeHandler() {
        Parties.LOGGER.error("Cold Sweat API wasn't found. Disabling support...");
        disableSupport();
    }
}
