package io.sedu.mc.parties.api.mod.openpac;

import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.fml.ModList;

public class PACCompatManager {
    private static IPACHandler handler = FakeHandler.INST;
    public static IPACHandler getHandler() {
        return handler;
    }
    private static boolean active = false;

    public static void init() {
        if (ModList.get().isLoaded("openpartiesandclaims")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        handler = new PACHandler();
        active = true;
    }
}
