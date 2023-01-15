package io.sedu.mc.parties.network;

import io.sedu.mc.parties.Parties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PartiesPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    public static void registerPackets() {

        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Parties.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();


        INSTANCE.messageBuilder(ClientPacketData.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientPacketData::new)
                .encoder(ClientPacketData::encode)
                .consumer(ClientPacketData::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }


}
