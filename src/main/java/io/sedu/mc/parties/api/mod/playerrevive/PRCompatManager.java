package io.sedu.mc.parties.api.mod.playerrevive;

import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class PRCompatManager {
    private static IPRHandler handler = FakeHandler.INST;


    public static IPRHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("playerrevive") && !ModList.get().isLoaded("incapacitated")) initCompat();
    }

    private static void initCompat() {
        if (PRHandler.exists()) {
            handler = new PRHandler();
            MinecraftForge.EVENT_BUS.register(PREventHandler.class);
        }
    }
}
