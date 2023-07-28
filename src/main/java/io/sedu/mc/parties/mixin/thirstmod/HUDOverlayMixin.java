package io.sedu.mc.parties.mixin.thirstmod;

import dev.ghen.thirst.foundation.gui.appleskin.HUDOverlayHandler;
import io.sedu.mc.parties.api.mod.thirstmod.TMCompatManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HUDOverlayHandler.class)
public abstract class HUDOverlayMixin {

    @Inject(at = @At("HEAD"), method = "renderThirstOverlay", remap = false, cancellable = true)
    private static void shouldDisplayThirstOverlay(CallbackInfo callback) {
        if (!TMCompatManager.enableOverlay) callback.cancel();
    }

    @Inject(at = @At("HEAD"), method = "renderExhaustion", remap = false, cancellable = true)
    private static void shouldDisplayExhaustOverlay(CallbackInfo callback) {
        if (!TMCompatManager.enableOverlay) callback.cancel();
    }

}
