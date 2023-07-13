package io.sedu.mc.parties.mixin;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.spells.CastSource;
import io.sedu.mc.parties.api.mod.ironspellbooks.ISSEventHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerMagicData.class)
public abstract class PlayerMagicDataMixin {

    @Shadow private ServerPlayer serverPlayer;

    @Inject(at = @At("RETURN"), method = "initiateCast", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private void serverCastListener(int spellId, int spellLevel, int castDuration, CastSource castSource,
                                           CallbackInfo ci) {

        //serverPlayer being null could mean we are on the client, but it's not 100% guaranteed?
        //But if it's not null, we're definitely on the server.
        if (serverPlayer == null) return;
        ISSEventHandler.onServerSpellCast(serverPlayer.getUUID(), spellId, castDuration);
    }

    @Inject(at = @At("RETURN"), method = "resetCastingState", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private void serverCastFinishedListener(CallbackInfo ci) {

        //serverPlayer being null could mean we are on the client, but it's not 100% guaranteed?
        //But if it's not null, we're definitely on the server.
        if (serverPlayer == null) return;
        ISSEventHandler.onServerCastFinished(serverPlayer.getUUID());
        //ISSEventHandler.onClientSpellCast(spellId, castDuration);
    }
}
