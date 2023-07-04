package io.sedu.mc.parties.mixin;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.common.capabilities.OriginContainer;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.origins.OCommonEventHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(OriginContainer.class)
public abstract class ChooseOriginMixin {

    @Final
    @Shadow(remap = false)
    private Player player;

    @Inject(method="setOrigin", at = @At(value = "TAIL"), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private void originChangeListener(OriginLayer layer, Origin origin, CallbackInfo ci, Origin previous) {
        if (player != null && origin != Origin.EMPTY) {
            if (previous != null) {
                if (previous.getOrder() >= origin.getOrder()) {
                    Parties.LOGGER.debug("{} has been assigned a new main origin: {}", player.getScoreboardName(), origin.getRegistryName());
                    OCommonEventHandler.changeOrigin(player.getUUID(), Objects.requireNonNull(origin.getRegistryName()).toString());
                }
            } else {
                Parties.LOGGER.debug("{} has been assigned a new main origin: {}", player.getScoreboardName(), origin.getRegistryName());
                OCommonEventHandler.changeOrigin(player.getUUID(), Objects.requireNonNull(origin.getRegistryName()).toString());
            }
        }
    }
}
