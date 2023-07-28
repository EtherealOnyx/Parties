package io.sedu.mc.parties.api.mod.hardcorerevival;

import io.sedu.mc.parties.api.mod.hardcorerevival.HREventHandler;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.HardcoreRevivalManager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HardcoreRevivalManager.class)
public abstract class MixinHRManager {


    @Inject(at = @At("RETURN"), method = "wakeup(Lnet/minecraft/world/entity/player/Player;Z)V", remap = false)
    private void wakeup(Player player, boolean applyEffects, CallbackInfo callback) {
        HREventHandler.sendWakeUpEffect(player);
    }

    @Inject(at = @At("HEAD"), method = "abortRescue(Lnet/minecraft/world/entity/player/Player;)V", remap = false)
    private void abortRescue(Player player, CallbackInfo callback) {
        HREventHandler.abortRescue(HardcoreRevival.getRevivalData(player).getRescueTarget());
    }
}
