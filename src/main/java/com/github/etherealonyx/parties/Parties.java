package com.github.etherealonyx.parties;

import com.github.etherealonyx.parties.data.client.PacketHelper;
import com.github.etherealonyx.parties.events.PartyEvent;
import com.github.etherealonyx.parties.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MODID)
public class Parties {
    public static Parties instance;
    private static final Logger LOGGER = LogManager.getLogger(Reference.MODID);

    public Parties() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }

    public void init(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(PartyEvent.instance);
        NetworkHandler.registerPackets();
    }

}
