package eomods.combatoverhaul.eoparties;

import eomods.combatoverhaul.eoparties.events.PartyEvent;
import eomods.combatoverhaul.eoparties.network.COPSHandler;
import eomods.combatoverhaul.eoparties.proxy.ClientProxy;
import eomods.combatoverhaul.eoparties.proxy.IProxy;
import eomods.combatoverhaul.eoparties.proxy.ServerProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

@Mod("eo_parties")
public class COPartySystem {
    private static Logger logger;

    public static IProxy proxy = DistExecutor.runForDist(()->()->new ClientProxy(), () -> ServerProxy::new);

    public COPartySystem() {
        COPSHandler.registerMessages();
        MinecraftForge.EVENT_BUS.register(new PartyEvent());
    }

}