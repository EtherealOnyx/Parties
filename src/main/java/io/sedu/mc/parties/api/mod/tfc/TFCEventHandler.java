package io.sedu.mc.parties.api.mod.tfc;

import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.events.ClientEvent;
import io.sedu.mc.parties.network.InfoPacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TFCEventHandler {

    @SubscribeEvent
    public static void onPartyJoin(PartyJoinEvent event) {
        event.forTrackersAndSelf((sendTo, propOf) -> {
            ServerPlayerData p = ServerPlayerData.playerList.get(propOf);
            InfoPacketHelper.sendWorldTempUpdate(sendTo, propOf, p.getWorldTemp());
            InfoPacketHelper.sendBodyTempUpdate(sendTo, propOf,p.getBodyTemp());
        });
    }

    @SubscribeEvent
    public static void ticker(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().isPaused()) return;
            if (ClientEvent.tick % 20 == 2) {
                ClientPlayerData.getSelf(ClientPlayerData::updateThirstTFC);
            }
        }
    }
}
