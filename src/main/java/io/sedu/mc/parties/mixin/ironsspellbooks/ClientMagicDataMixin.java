package io.sedu.mc.parties.mixin.ironsspellbooks;


import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.spells.CastSource;
import io.sedu.mc.parties.api.mod.ironspellbooks.ISSEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(ClientMagicData.class)
public abstract class ClientMagicDataMixin {

    @Inject(at = @At("RETURN"), method = "setClientCastState", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void clientCastStateListener(int spellId, int spellLevel, int castDuration, CastSource castSource, CallbackInfo ci) {
        ISSEventHandler.onClientSpellCast(spellId, castDuration);
    }

    @Inject(at = @At("RETURN"), method = "resetClientCastState", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void clientCastFinishListener(UUID playerUUID, CallbackInfo ci, KeyframeAnimationPlayer animationPlayer) {
        ISSEventHandler.onClientSpellFinish(playerUUID);
    }
}
