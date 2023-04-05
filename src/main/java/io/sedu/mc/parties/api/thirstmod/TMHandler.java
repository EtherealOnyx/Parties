package io.sedu.mc.parties.api.thirstmod;

import dev.ghen.thirst.foundation.common.capability.IThirstCap;
import io.sedu.mc.parties.Parties;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class TMHandler implements ITMHandler {
    private static final Capability<IThirstCap> THIRSTY = CapabilityManager.get(new CapabilityToken<>(){});

    static {
        Parties.LOGGER.info("Initializing Compatibility with Thirst was Taken (Thirst Mod).");
        if (THIRSTY == null) {
            Parties.LOGGER.warn("Failed to load Thirst Mod compatibility...");
        }
    }

    public static boolean exists() {
        return THIRSTY != null;
    }

    @Override
    public int getThirst(Player player) {
        IThirstCap thirst = player.getCapability(THIRSTY).orElse(null);
        //noinspection ConstantConditions
        if (thirst != null) {
            return thirst.getThirst();
        }
        return 0;
    }
}
