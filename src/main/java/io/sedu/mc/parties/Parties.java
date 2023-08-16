package io.sedu.mc.parties;

//import io.sedu.mc.parties.client.PartyOverlay;

import io.sedu.mc.parties.events.PartyEvent;
import io.sedu.mc.parties.setup.ClientSetup;
import io.sedu.mc.parties.setup.ConfigSetup;
import io.sedu.mc.parties.setup.ModSetup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Parties.MODID)
public class Parties
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "sedparties";

    public Parties() {

        // Register the deferred registry
        //ModSetup.setup();
        //Registration.init();
        ConfigSetup.register();

        // Register the setup method for mod-loading
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(ModSetup::init);
        MinecraftForge.EVENT_BUS.register(PartyEvent.class);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modbus.addListener(ClientSetup::init);
            modbus.addListener(ClientSetup::postInit);
        }

        );
    }
}
