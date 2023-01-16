package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Parties.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static void init(FMLCommonSetupEvent event) {
        PartiesPacketHandler.register();
    }
}