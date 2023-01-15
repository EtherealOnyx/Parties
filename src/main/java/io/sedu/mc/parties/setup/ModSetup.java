package io.sedu.mc.parties.setup;

import io.sedu.mc.parties.network.PartiesPacketHandler;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {
    public static void init(final FMLCommonSetupEvent event) {
        PartiesPacketHandler.registerPackets();
    }
}