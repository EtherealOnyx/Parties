package io.sedu.mc.parties.mixin;

import de.cas_ual_ty.spells.client.ClientMessageHandler;
import de.cas_ual_ty.spells.network.ManaSyncMessage;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientMessageHandler.class)
public abstract class ClientMessageHandlerMixin {

    @Inject(method = "handleManaSync(Lde/cas_ual_ty/spells/network/ManaSyncMessage;)V", at = @At(value = "INVOKE", target = "Lde/cas_ual_ty/spells/capability/ManaHolder;getManaHolder(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraftforge/common/util/LazyOptional;", shift = At.Shift.AFTER), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void handleManaSync(ManaSyncMessage msg, CallbackInfo callback, Level level) {
        if (level.getEntity(msg.entityId()) instanceof Player p) {
            PlayerAPI.getClientPlayer(p.getUUID(), cPD -> cPD.getManaSS(mana -> mana.checkAnim(msg.mana(), mana.max, msg.extraMana())));

        }

    }

}
