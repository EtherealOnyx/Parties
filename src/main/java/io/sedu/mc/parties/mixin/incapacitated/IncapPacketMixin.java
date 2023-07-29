package io.sedu.mc.parties.mixin.incapacitated;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.networking.IncapPacket;
import io.sedu.mc.parties.api.helper.PlayerAPI;
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

@Mixin(IncapPacket.class)
public class IncapPacketMixin {

    @Inject(at = @At("RETURN"), method = "<init>(IZS)V", remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void incapacitationListener(int entityId, boolean isIncapacitated, short downCount, CallbackInfo ci) {
        ServerPlayerData s = PlayerAPI.getPlayerFromId(entityId);
        if (s != null) {

            ServerPlayer p = s.getPlayer();
            if (p != null) {
                //Check Capability
                p.getCapability(PlayerCapability.INSTANCE).ifPresent(incap -> {
                    if (incap.getIsIncapacitated()) { //playerKnockedOut
                        HashMap<UUID, Boolean> trackers;
                        InfoPacketHelper.sendDowned(s.getPlayer(), true, incap.getTicksUntilDeath());
                        if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                            trackers.forEach((id, serverTracked) -> {
                                InfoPacketHelper.sendDowned(id, p.getUUID(), true, incap.getTicksUntilDeath());
                                if (serverTracked)
                                    InfoPacketHelper.sendHealth(id, p.getUUID(), p.getHealth());
                            });
                        }
                        ServerPlayerData.playerList.get(p.getUUID()).setDowned(true);
                    } else { //sendWakeUp Effect
                        HashMap<UUID, Boolean> trackers;
                        //Make the timer reset when they wake up...
                        InfoPacketHelper.sendDowned(p, false, 0);
                        if ((trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                            trackers.forEach((id, serverTracked) -> {
                                InfoPacketHelper.sendDowned(id, p.getUUID(), false, 0);
                                if (serverTracked)
                                    InfoPacketHelper.sendHealth(id, p.getUUID(), p.getHealth());
                            });
                        }
                        ServerPlayerData.playerList.get(p.getUUID()).setDowned(false);

                    }

                });


            }
        }
    }

}
