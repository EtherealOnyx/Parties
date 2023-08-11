package io.sedu.mc.parties.api.mod.origins;

import io.sedu.mc.parties.api.mod.FakeHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class OCompatManager {
    private static IOHandler handler = FakeHandler.INST;
    protected static OriginCheckEvent eventInstance;

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
        MinecraftForge.EVENT_BUS.register(new OCommonEventHandler());
    }

    public static void initClientEvent() {
        eventInstance = new OriginCheckEvent((OHandler) handler);
    }
}
