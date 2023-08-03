package io.sedu.mc.parties.api.mod.gamestages;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.appleskin.ASEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class GSCompatManager {
    public static void init() {
        if (ModList.get().isLoaded("gamestages")) initCompat();
    }

    private static void initCompat() {
        Parties.LOGGER.info("[Parties] Initializing Compatibility with Game Stages.");
        MinecraftForge.EVENT_BUS.register(GSEventHandler.class);
    }
}
