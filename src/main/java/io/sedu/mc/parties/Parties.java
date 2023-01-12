package io.sedu.mc.parties;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Parties.MODID)
public class Parties
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "sedparties";

    public Parties() {

        // Register the deferred registry
        //ModSetup.setup();
        //Registration.init();
        //Config.register();

        // Register the setup method for modloading
        //IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        //modbus.addListener(ModSetup::init);
        //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientSetup::init));
    }
}
