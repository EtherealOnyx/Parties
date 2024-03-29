package io.sedu.mc.parties.mixin.arsnouveau;

import com.hollingsworth.arsnouveau.client.gui.GuiManaHUD;
import io.sedu.mc.parties.api.mod.arsnoveau.ANHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(GuiManaHUD.class)
public abstract class GuiManaMixin {
    @Inject(at = @At("HEAD"), method = "shouldDisplayBar()Z", remap = false, cancellable = true)
    private void displayOverlay(CallbackInfoReturnable<Boolean> callback) {
        if (!ANHandler.manaEnabled) callback.setReturnValue(false);
    }
}

