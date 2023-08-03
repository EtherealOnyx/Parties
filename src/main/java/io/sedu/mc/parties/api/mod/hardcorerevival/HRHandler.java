package io.sedu.mc.parties.api.mod.hardcorerevival;

import io.sedu.mc.parties.Parties;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.capability.HardcoreRevivalData;
import net.blay09.mods.hardcorerevival.config.HardcoreRevivalConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.util.function.BiConsumer;

public class HRHandler implements IHRHandler {


    static {
        Parties.LOGGER.info("[Parties] Initializing Compatibility with Hardcore Revival.");
    }

    @Override
    public void getDowned(Player player, BiConsumer<Boolean, Integer> action) {
        action.accept(((HardcoreRevival.getRevivalData(player)).isKnockedOut()), (HardcoreRevivalConfig.getActive().ticksUntilDeath - HardcoreRevival.getRevivalData(player).getKnockoutTicksPassed())/20);
    }

    @Override
    public void getReviveProgress(Player player, BiConsumer<Float, Player> action) {
        HardcoreRevivalData revivalData = HardcoreRevival.getRevivalData(player);
        if (revivalData.getRescueTarget() != null) {
            action.accept((float) revivalData.getRescueTime() / HardcoreRevivalConfig.getActive().rescueActionTicks, revivalData.getRescueTarget());
        }
    }

    public static boolean exists() {
        return (ModList.get().isLoaded("hardcorerevival"));
    }
}
