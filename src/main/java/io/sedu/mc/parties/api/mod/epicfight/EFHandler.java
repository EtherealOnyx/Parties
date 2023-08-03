package io.sedu.mc.parties.api.mod.epicfight;

import io.sedu.mc.parties.Parties;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.function.BiConsumer;

public class EFHandler implements IEFHandler {

    private static final Capability<EntityPatch> STAM = CapabilityManager.get(new CapabilityToken<>(){});

    public static boolean exists() {
       return STAM != null;
    }

    static {
        Parties.LOGGER.info("[Parties] Initializing Compatibility with Epic Fight Mod.");
        if (STAM == null) {
            Parties.LOGGER.error("[Parties] Failed to load Epic Fight Mod compatibility...");
        }
    }

    @Override
    public void getClientValues(Player player, BiConsumer<Float, Integer> action) {
        PlayerPatch p = (PlayerPatch) player.getCapability(STAM).orElse(null);
        if (p != null)
            action.accept(p.getStamina(), (int) p.getMaxStamina());
    }
}
