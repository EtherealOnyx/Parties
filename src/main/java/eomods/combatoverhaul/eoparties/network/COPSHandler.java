package eomods.combatoverhaul.eoparties.network;

import eomods.combatoverhaul.eoparties.lib.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class COPSHandler {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
        INSTANCE.registerMessage(HandlerData.class, PacketData.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(HandlerName.class, PacketName.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(HandlerServer.class, PacketServer.class, 2, Side.SERVER);
    }
}
