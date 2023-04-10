package io.sedu.mc.parties.api.arsnoveau;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.events.PartyJoinEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerSlowUpdateInterval;

public class ANEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerSlowUpdateInterval.get() == 0) {
                HashMap<UUID, Boolean> trackers;
                if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                    UUID player;
                    float mana;
                    boolean update = PlayerData.playerList.get(player = e.player.getUUID()).setMana(mana = ANCompatManager.getHandler().getCurrentMana(e.player));
                    if (update)
                        trackers.forEach((id, serverTracked) -> {
                                InfoPacketHelper.sendManaUpdate(id, player, mana);
                        });

                    int max;
                    boolean update2 = PlayerData.playerList.get(player).setMax(max = ANCompatManager.getHandler().getMaxMana(e.player));
                    if (update2)
                        trackers.forEach((id, serverTracked) -> {
                                InfoPacketHelper.sendMaxManaUpdate(id, player, max);
                        });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> {
            PlayerData p = PlayerData.playerList.get(propOf);
            InfoPacketHelper.sendManaUpdate(sendTo, propOf, p.getCurrentMana());
            InfoPacketHelper.sendMaxManaUpdate(sendTo, propOf, p.getMaxMana());
        });
    }

    @SubscribeEvent
    public static void castEvent(SpellCastEvent event) {
        if (event.getEntity() instanceof Player p) {
            HashMap<UUID, Boolean> trackers;
            float mana;
            UUID player;
            PlayerData.playerList.get(player = p.getUUID()).setMana(mana = Math.max(0, ANCompatManager.getHandler().getCurrentMana(p) - event.spell.getCastingCost()));
            InfoPacketHelper.sendManaUpdate(player, player, mana);
            if ((trackers = PlayerData.playerTrackers.get(p.getUUID())) != null) {
                trackers.forEach((id, serverTracked) -> {
                        InfoPacketHelper.sendManaUpdate(id, player, mana);
                });
            }
        }

    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().isPaused()) return;
            if (ClientEvent.tick % 20 == 2) {
                if (ClientPlayerData.playerList.size() > 0) {
                    ClientPlayerData.getSelf(ClientPlayerData::updateMana);
                }
            }
        }
    }
}
