package io.sedu.mc.parties.api.mod.thirstmod;

import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.gui.ThirstBarRenderer;
import io.sedu.mc.parties.Parties;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class TMHandler implements ITMHandler {
    private static final Capability<IThirst> THIRSTY = CapabilityManager.get(new CapabilityToken<>(){});

    static {
        Parties.LOGGER.info("[Parties] Initializing Compatibility with Thirst was Taken (Thirst Mod).");
        if (THIRSTY == null) {
            Parties.LOGGER.warn("[Parties] Failed to load Thirst Mod compatibility...");
        }
    }

    public static boolean exists() {
        return THIRSTY != null;
    }

    @Override
    public int getThirst(Player player) {
        IThirst thirst = player.getCapability(THIRSTY).orElse(null);
        //noinspection ConstantConditions
        if (thirst != null) {
            return thirst.getThirst();
        }
        return 0;
    }

    @Override
    public void setThirstRender(Boolean aBoolean) {
        OverlayRegistry.enableOverlay(ThirstBarRenderer.THIRST_OVERLAY, aBoolean);
        TMCompatManager.enableOverlay = aBoolean;
    }

    @Override
    public int getQuench(Player player) {
        IThirst thirst = player.getCapability(THIRSTY).orElse(null);
        //noinspection ConstantConditions
        if (thirst != null) {
            return thirst.getQuenched();
        }
        return 0;
    }
}
