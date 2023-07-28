package io.sedu.mc.parties.api.mod.arsnoveau;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import io.sedu.mc.parties.Parties;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.function.BiConsumer;

public class ANHandler implements IANHandler {

    private static final Capability<IManaCap> MANA = CapabilityManager.get(new CapabilityToken<>(){});
    static {
        Parties.LOGGER.info("Initializing Compatibility with Ars Nouveau.");
    }

    public boolean exists() {
        return MANA != null;
    }

    @Override
    public void getManaValues(Player player, BiConsumer<Float, Integer> action) {
        IManaCap mana = player.getCapability(MANA).orElse(null);
        //noinspection ConstantConditions
        if (mana != null) {
           action.accept((float) mana.getCurrentMana(), mana.getMaxMana());
        }
    }

    public static boolean manaEnabled = true;
    @Override
    public void setManaRender(Boolean renderMana) {
        manaEnabled = renderMana;
    }


    @Override
    public float getCurrentMana(Player player) {
        IManaCap mana = player.getCapability(MANA).orElse(null);
        //noinspection ConstantConditions
        if (mana != null) {
            return (float) mana.getCurrentMana();
        }
        return 0;
    }

    @Override
    public int getMaxMana(Player player) {
        IManaCap mana = player.getCapability(MANA).orElse(null);
        //noinspection ConstantConditions
        if (mana != null) {
            return mana.getMaxMana();
        }
        return 0;
    }
}
