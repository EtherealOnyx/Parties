package io.sedu.mc.parties.api.mod.ironspellbooks;

import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerSlowUpdateInterval;

public class ISSEventHandler {

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            if (e.player.tickCount % playerSlowUpdateInterval.get() == 9) {
                ISSCompatManager.getHandler().getServerMana((ServerPlayer) e.player, (cur, max) -> {
                    HashMap<UUID, Boolean> trackers;
                    if ((trackers = PlayerData.playerTrackers.get(e.player.getUUID())) != null) {
                        UUID player;
                        PlayerData pd;
                        (pd = PlayerData.playerList.get(player = e.player.getUUID())).setManaI(cur, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendManaUpdateI(id, player, cur)));
                        pd.setMaxManaI(max, () -> trackers.forEach((id, serverTracked) -> InfoPacketHelper.sendMaxManaUpdateI(id, player, max)));

                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        IISSHandler h = ISSCompatManager.getHandler();
        event.forTrackersAndSelf((sendTo, propOf) -> PlayerAPI.getServerPlayer(propOf, p -> h.getServerMana(p, (cur, max) -> {
            InfoPacketHelper.sendManaUpdateI(sendTo, propOf, cur);
            InfoPacketHelper.sendMaxManaUpdateI(sendTo, propOf, max);
        })));
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().isPaused()) return;
            if (ClientEvent.tick % 40 == 0) {
                if (ClientPlayerData.playerList.size() > 0) {
                    ClientPlayerData.getSelf(ClientPlayerData::updateManaISS);
                }
            }
        }
    }

    public static void onSpellCast() {

    }

    public static void onClientSpellCast(int spellId, int castDuration) {
        ClientPlayerData.getSelf(clientPlayerData -> clientPlayerData.initSpell(spellId, castDuration));
    }

    public static void onClientSpellFinish() {
        ClientPlayerData.getSelf(ClientPlayerData::finishSpell);
    }
}
