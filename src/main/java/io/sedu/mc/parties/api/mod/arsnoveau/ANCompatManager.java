package io.sedu.mc.parties.api.mod.arsnoveau;

import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class ANCompatManager {
    private static IANHandler handler = FakeHandler.INST;


    public static IANHandler getHandler() {
        return handler;
    }

    public static void init() {
        if (ModList.get().isLoaded("ars_nouveau")) initCompat();
    }

    private static void initCompat() {
        IANHandler newHandler = new ANHandler();
        if (newHandler.exists()) {
            handler = newHandler;
            MinecraftForge.EVENT_BUS.register(ANEventHandler.class);
        }

    }
}
