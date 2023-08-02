package io.sedu.mc.parties.mixin.incapacitated;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.api.mod.incapacitated.IEventHandler;
import io.sedu.mc.parties.data.ServerPlayerData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(IncapPacket.class)
public class IncapPacketMixin {

    @Inject(at = @At("TAIL"), method = "<init>(IZS)V", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private void incapacitationListener(int entityId, boolean isIncapacitated, short downCount, CallbackInfo ci) {
        ServerPlayerData s = PlayerAPI.getPlayerFromId(entityId);
        Parties.LOGGER.info(s);
        if (s != null) {

            //TODO: Test where this fails.
            ServerPlayer p = s.getPlayer();
            Parties.LOGGER.info(p);
            if (p != null) {
                //Check Capability
                p.getCapability(PlayerCapability.INSTANCE).ifPresent(incap -> {
                    Parties.LOGGER.info(incap);
                    if (incap.getIsIncapacitated())
                        IEventHandler.incapacitate(p, incap.getTicksUntilDeath() / 20);
                    else
                        IEventHandler.wakeUp(p);
                });


            }
        }
    }

}
