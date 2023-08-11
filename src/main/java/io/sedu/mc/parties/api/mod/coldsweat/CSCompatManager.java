package io.sedu.mc.parties.api.mod.coldsweat;

import dev.momostudios.coldsweat.config.ColdSweatConfig;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class CSCompatManager {
    private static ICSHandler handler = FakeHandler.INST;
    private static boolean active = false;


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
            active = true;
        }

    }

    public static boolean active() {
        return active;
    }

    public static void disableSupport() {
        handler = FakeHandler.INST;
        MinecraftForge.EVENT_BUS.unregister(CSEventHandler.class);
        active = false;
    }

    public static void changeHandler() {
        Parties.LOGGER.error("[Parties] Cold Sweat API wasn't found. Disabling support...");
        disableSupport();
    }
}
