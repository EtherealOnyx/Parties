package io.sedu.mc.parties.api.mod.origins;

import net.minecraftforge.fml.ModList;

public class IOCompatManager {
    private static IOHandler handler = new OHandlerFake();
    public static IOHandler getHandler() {
        return handler;
    }
    private static boolean active = false;

    public static void init() {
        if (ModList.get().isLoaded("origins")) initCompat();
    }

    public static boolean active() {
        return active;
    }

    private static void initCompat() {
        handler = new OHandler();
        active = true;
    }
}
