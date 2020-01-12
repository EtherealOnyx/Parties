package eomods.combatoverhaul.eoparties.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static eomods.combatoverhaul.eoparties.Parties.*;

public class Handler {

    public static SimpleChannel network;

    public static void registerPackets() {
        //Register network handlers.
        network = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MOD_CHANNEL), () -> IP,
                IP::equalsIgnoreCase, IP::equalsIgnoreCase);
        network.registerMessage(0, ClientPacketData.class, ClientPacketData::encode, ClientPacketData::new,
                ClientPacketData::handle);
        network.registerMessage(1, ClientPacketName.class, ClientPacketName::encode, ClientPacketName::new,
                ClientPacketName::handle);
        network.registerMessage(2, ServerPacketData.class, ServerPacketData::encode, ServerPacketData::new,
                ServerPacketData::handle);
    }
}
