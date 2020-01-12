package eomods.combatoverhaul.eoparties;

import eomods.combatoverhaul.eoparties.events.PartyEvent;
import eomods.combatoverhaul.eoparties.network.Handler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Parties.MODID)
public class Parties {
    public static final String MODID = "eo_parties";
    public static final String NAME = "Parties";
    public static Parties instance;

    private static Logger logger = LogManager.getLogger(MODID);

    public static final String IP = "1.0.0";
    public static final String MOD_CHANNEL = "c_eoparties";


    public Parties() {
        instance = this;
        //ModLoadingContext.get().registerConfig()
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::initClient);

    }

    public void init(final FMLCommonSetupEvent event) {
        Handler.registerPackets();
        MinecraftForge.EVENT_BUS.register(PartyEvent.instance);
    }

    public void initClient(final FMLClientSetupEvent event) {
        //Register client stuff in future.
    }

}