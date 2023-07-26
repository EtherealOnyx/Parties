package io.sedu.mc.parties.mixin.ironsspellbooks;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.sedu.mc.parties.api.mod.ironspellbooks.ISSCompatManager;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.UUID;

@Mixin(MagicManager.class)
public abstract class MagicManagerMixin {

    @Inject(at = @At("RETURN"), method = "setPlayerCurrentMana", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private void playerManaListener(ServerPlayer serverPlayer, int newManaValue, CallbackInfo ci, PlayerMagicData playerMagicData) {
        ISSCompatManager.getHandler().getServerMana(serverPlayer, (cur, max) -> {
            HashMap<UUID, Boolean> trackers;
            if ((trackers = ServerPlayerData.playerTrackers.get(serverPlayer.getUUID())) != null) {
                UUID player;
                ServerPlayerData pd;
                (pd = ServerPlayerData.playerList.get(player = serverPlayer.getUUID())).setManaI(cur, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendManaUpdateI(id, player, cur)));
                pd.setMaxManaI(max, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendMaxManaUpdateI(id, player, max)));

            }
        });
    }
}
