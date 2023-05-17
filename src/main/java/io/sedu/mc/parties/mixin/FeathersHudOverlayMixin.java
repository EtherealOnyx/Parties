package io.sedu.mc.parties.mixin;

import com.elenai.feathers.client.gui.FeathersHudOverlay;
import io.sedu.mc.parties.api.mod.feathers.FCompatManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeathersHudOverlay.ClientModBusEvents.class)
public abstract class FeathersHudOverlayMixin {
    @Inject(at = @At("HEAD"), method = "registerGuiOverlays", remap = false, cancellable = true)
    private static void shouldDisplayOverlay(CallbackInfo callback) {
        if (!FCompatManager.enableOverlay) callback.cancel();
    }
}
