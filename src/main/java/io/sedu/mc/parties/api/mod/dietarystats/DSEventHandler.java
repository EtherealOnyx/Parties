package io.sedu.mc.parties.api.mod.dietarystats;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

public class DSEventHandler {


    @SubscribeEvent
    public static void onFoodConsumption(LivingEntityUseItemEvent.Finish event) {
        if (!event.getItem().isEdible()) return;
        if (!event.getEntityLiving().level.isClientSide()) {
            HashMap<UUID, Boolean> trackers;
            if (event.getEntity() instanceof Player p && (trackers = ServerPlayerData.playerTrackers.get(p.getUUID())) != null) {
                DSCompatManager.getHandler().getMaxHunger(p, food -> {
                    ServerPlayerData.playerList.get(p.getUUID()).setMaxHunger(food, () -> trackers.forEach((id, serverTracked) -> {
                        InfoPacketHelper.sendMaxHungerUpdate(id, p.getUUID(), food);
                    }));
                });
            }
        } else {
            //Client Side
            if (event.getEntity() instanceof Player p) {
                PlayerAPI.getClientPlayer(p.getUUID(), player -> DSCompatManager.getHandler().getMaxHunger(p, (max) -> {
                    if (max > 0) {
                        Parties.LOGGER.debug("Setting max hunger on client-side.");
                        player.setMaxHunger(max);
                    }
                }));
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> PlayerAPI.getPlayer(propOf, p -> InfoPacketHelper.sendMaxHungerUpdate(sendTo, propOf, p.getMaxHunger())));
    }
}
