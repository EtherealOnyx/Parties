package io.sedu.mc.parties.mixin.ironsspellbooks;

import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(ClientSpellCastHelper.class)
public abstract class ClientCastHelperMixin {

    @Inject(at = @At("RETURN"), method = "handleClientBoundOnCastFinished", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void clientCastFinishedListener(UUID castingEntityId, SpellType spellType, boolean cancelled, CallbackInfo ci, Player player) {
        if (!cancelled) ClientPlayerData.getSelf(ClientPlayerData::updateManaISS);
    }
}
