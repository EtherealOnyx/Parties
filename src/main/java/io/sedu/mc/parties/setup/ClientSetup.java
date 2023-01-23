package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.client.PartyOverlay;
import io.sedu.mc.parties.client.RenderData;
import io.sedu.mc.parties.events.PartyEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static io.sedu.mc.parties.Parties.MODID;
import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "party", PartyOverlay.HUD_PARTY);
        MinecraftForge.EVENT_BUS.addListener(PartyEvent::onClientLeave);
        //MinecraftForge.EVENT_BUS.addListener(PartyEvent::onClientJoin);
        RenderData.setDefaultData();
    }
}