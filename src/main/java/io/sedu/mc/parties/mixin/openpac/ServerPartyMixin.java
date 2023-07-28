package io.sedu.mc.parties.mixin.openpac;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.openpac.PACCompatManager;
import io.sedu.mc.parties.data.PartySaveData;
import io.sedu.mc.parties.data.ServerConfigData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xaero.pac.common.parties.party.ally.api.IPartyAllyAPI;
import xaero.pac.common.parties.party.api.IPartyPlayerInfoAPI;
import xaero.pac.common.parties.party.member.PartyMember;
import xaero.pac.common.parties.party.member.PartyMemberRank;
import xaero.pac.common.parties.party.member.api.IPartyMemberAPI;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.parties.party.ServerParty;
import xaero.pac.common.server.parties.party.api.IServerPartyAPI;

import java.util.UUID;

@Mixin(ServerParty.class)
public abstract class ServerPartyMixin {


   @Inject(method = "changeOwner", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void changeOwnerListener(UUID newOwnerId, String newOwnerUsername, CallbackInfoReturnable<Boolean> cir,
                             PartyMember oldOwner, boolean result) {
        if (result && ServerConfigData.isPartySyncEnabled()) {
            PACCompatManager.getHandler().changeLeader(oldOwner.getUUID(), newOwnerId);
        }
    }

    @Inject(method = "addMember(Ljava/util/UUID;Lxaero/pac/common/parties/party/member/PartyMemberRank;Ljava/lang/String;)Lxaero/pac/common/parties/party/member/PartyMember;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void addMemberListener(UUID memberUUID, PartyMemberRank rank, String playerUsername,
                           CallbackInfoReturnable<PartyMember> cir) {
        if (cir.getReturnValue() != null && ServerConfigData.isPartySyncEnabled()) {
            //Success?
            IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> p = OpenPACServerAPI.get(PartySaveData.server).getPartyManager().getPartyByMember(memberUUID);
            if (p != null) {
                PACCompatManager.getHandler().memberAdded(p.getOwner().getUUID(), memberUUID, p.getId());
            } else {
                Parties.LOGGER.error("Error adding party member through Open-PAC syncing!");
            }
        }
    }

    @Inject(method = "removeMember(Ljava/util/UUID;)Lxaero/pac/common/parties/party/member/PartyMember;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void removeMemberListener(UUID memberUUID, CallbackInfoReturnable<PartyMember> cir, PartyMember m) {
        if (cir.getReturnValue() != null && ServerConfigData.isPartySyncEnabled()) {
            //Success?
            PACCompatManager.getHandler().memberLeft(memberUUID);
        }
    }
}
