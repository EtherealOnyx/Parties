package io.sedu.mc.parties.api.mod.spellsandshields;

import de.cas_ual_ty.spells.capability.IManaHolder;
import de.cas_ual_ty.spells.capability.ManaHolder;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class SSHandler implements ISSHandler {

    private static final Capability<IManaHolder> MANA = CapabilityManager.get(new CapabilityToken<>(){});

    public static boolean exists() {
       return MANA != null;
    }

    static {
        Parties.LOGGER.info("Initializing Compatibility with Spells and Shields Mod.");
        if (MANA == null) {
            Parties.LOGGER.error("Failed to load Spells and Shields compatibility...");
        }
    }

    @Override
    public void getAllMana(Player player, TriConsumer<Float, Float, Float> action) {
        ManaHolder p = (ManaHolder) player.getCapability(MANA).orElse(null);
        if (p != null)
            action.accept(p.getMana(), p.getMaxMana(), p.getExtraMana());
    }

    @Override
    public float getMax(Player player) {
        ManaHolder p = (ManaHolder) player.getCapability(MANA).orElse(null);
        return p != null ? p.getMaxMana() : 20;
    }

    @Override
    public void setManaRender(Boolean aBoolean) {
        SSCompatManager.enableOverlay = aBoolean;
    }

}
