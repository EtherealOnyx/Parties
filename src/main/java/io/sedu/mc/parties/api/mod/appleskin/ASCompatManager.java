package io.sedu.mc.parties.api.mod.appleskin;

import io.sedu.mc.parties.Parties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class ASCompatManager {

    public static void init() {
        if (ModList.get().isLoaded("appleskin")) initCompat();
    }

    private static void initCompat() {
        Parties.LOGGER.info("Initializing Compatibility with AppleSkin.");
        MinecraftForge.EVENT_BUS.register(ASEventHandler.class);
    }
}
