package io.sedu.mc.parties.mixin;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.openpac.PACCompatManager;
import io.sedu.mc.parties.data.ServerConfigData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xaero.pac.common.server.parties.party.PartyManager;
import xaero.pac.common.server.parties.party.ServerParty;

@Mixin(PartyManager.class)
public abstract class PartyManagerMixin {
    @Inject(method = "createPartyForOwner(Lnet/minecraft/world/entity/player/Player;)Lxaero/pac/common/server/parties/party/ServerParty;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void createPartyForOwner(Player owner, CallbackInfoReturnable<ServerParty> cir) {
        if (cir.getReturnValue() != null && ServerConfigData.isPartySyncEnabled()) {
            PACCompatManager.getHandler().memberAdded(owner.getUUID(), owner.getUUID(), cir.getReturnValue().getId());
        }
    }

    @Inject(method = "removeParty(Lxaero/pac/common/server/parties/party/ServerParty;)V", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void removeParty(ServerParty party, CallbackInfo ci) {
        if (ServerConfigData.isPartySyncEnabled()) {
            if (party.isDestroyed()) {
                Parties.LOGGER.debug("Open-PAC Party destroyed, disbanding party...");
            }
            PACCompatManager.getHandler().disbandParty(party.getId());
        }
    }
}
