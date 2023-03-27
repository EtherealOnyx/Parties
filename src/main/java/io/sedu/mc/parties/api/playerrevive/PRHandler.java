package io.sedu.mc.parties.api.playerrevive;

import io.sedu.mc.parties.Parties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import team.creative.playerrevive.PlayerRevive;
import team.creative.playerrevive.api.IBleeding;

import java.util.function.BiConsumer;

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

    private static IBleeding getBleedingCapability(Player player) {
        if (player == null)
            return null;
        MinecraftServer server = player.getServer();
        if (server == null)
            return null;
        LazyOptional<IBleeding> bleeding = player.getCapability(BLEEDING);
        if (bleeding.isPresent() && server.isPublished())
            return bleeding.orElseThrow(RuntimeException::new);
        else
            return null;
    }

    @Override
    public void getBleed(Player player, BiConsumer<Boolean, Integer> action) {
        IBleeding revival = getBleedingCapability(player);
        if (revival != null) {
            action.accept(revival.isBleeding() && revival.timeLeft() > 0, revival.timeLeft()/20);
        }
    }

    @Override
    public float getReviveProgress(Player clientPlayer) {
       IBleeding bleed = clientPlayer.getCapability(BLEEDING).orElse(null);
       if (bleed == null)
           return 0f;
       return bleed.getProgress() / PlayerRevive.CONFIG.revive.requiredReviveProgress;
    }

}
