package io.sedu.mc.parties.api.mod.incapacitated;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.capability.IPlayerCapability;
import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IHandler implements IIHandler {
    //TODO: Timer counts down on revive when its not supposed to.

    @Override
    public void getReviveCount(Player p, Consumer<Integer> action) {
        if (p != null) {
            p.getCapability(PlayerCapability.INSTANCE).ifPresent(cap -> {
                if (cap.getIsIncapacitated()) {
                    action.accept(cap.getReviveCount());
                }
            });
        }
    }

    @Override
    public void getCompleteIncapInfo(Player p, BiConsumer<Float, Integer> action) {
        if (p != null) {
            p.getCapability(PlayerCapability.INSTANCE).ifPresent(cap -> {
                if (cap.getIsIncapacitated()) {
                    action.accept(((float) Incapacitated.config.REVIVETICKS.get() - cap.getReviveCount())
                                          / Incapacitated.config.REVIVETICKS.get(), cap.getTicksUntilDeath() / 20);
                }
            });
        }

    }

    @Override
    public int getDeathTicks(Player p) {
        if (p != null) {
            IPlayerCapability pCap = p.getCapability(PlayerCapability.INSTANCE).orElse(null);
            if (pCap != null && pCap.getIsIncapacitated()) {
                return pCap.getTicksUntilDeath() / 20;
            }
        }
        return 0;
    }
}
