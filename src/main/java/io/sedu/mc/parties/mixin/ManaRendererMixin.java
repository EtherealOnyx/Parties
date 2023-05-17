package io.sedu.mc.parties.mixin;

import de.cas_ual_ty.spells.client.ManaRenderer;
import io.sedu.mc.parties.api.mod.spellsandshields.SSCompatManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ManaRenderer.class)
public abstract class ManaRendererMixin {
    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraftforge/client/gui/ForgeIngameGui;Lcom/mojang/blaze3d/vertex/PoseStack;FII)V", remap = false, cancellable = true)
    private void shouldDisplayOverlay(CallbackInfo callback) {
        if (!SSCompatManager.enableOverlay) callback.cancel();
    }
}
