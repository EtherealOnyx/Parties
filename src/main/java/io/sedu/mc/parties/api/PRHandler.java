package io.sedu.mc.parties.api;

import io.sedu.mc.parties.Parties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import team.creative.playerrevive.api.IBleeding;

public class PRHandler implements IPRHandler {
    private static final Capability<IBleeding> BLEEDING = CapabilityManager.get(new CapabilityToken<>(){});

    static {
        Parties.LOGGER.info("Initializing Compatibility with PlayerRevive.");
        if (BLEEDING == null) {
            Parties.LOGGER.warn("Failed to load PlayerRevive compatibility...");
        }
    }

    public static boolean exists() {
        return BLEEDING != null;
    }

}
