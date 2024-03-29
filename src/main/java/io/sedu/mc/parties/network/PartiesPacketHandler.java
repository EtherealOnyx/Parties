package io.sedu.mc.parties.network;

import io.sedu.mc.parties.Parties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PartiesPacketHandler {
    public static SimpleChannel INSTANCE;

    // Every packet needs a unique ID (unique for this channel)
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static void register() {

        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Parties.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;


        net.messageBuilder(ClientPacketData.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                                  .decoder(ClientPacketData::new)
                                  .encoder(ClientPacketData::encode)
                                  .consumer(ClientPacketData::handle)
                                  .add();

        net.messageBuilder(ServerPacketData.class, id(), NetworkDirection.PLAY_TO_SERVER)
           .decoder(ServerPacketData::new)
           .encoder(ServerPacketData::encode)
           .consumer(ServerPacketData::handle)
           .add();

        net.messageBuilder(RenderPacketData.class, id(), NetworkDirection.PLAY_TO_CLIENT)
           .decoder(RenderPacketData::new)
           .encoder(RenderPacketData::encode)
           .consumer(RenderPacketData::handle)
           .add();

        net.messageBuilder(StringPacketData.class, id(), NetworkDirection.PLAY_TO_SERVER)
           .decoder(StringPacketData::new)
           .encoder(StringPacketData::encode)
           .consumer(StringPacketData::handle)
           .add();

        net.messageBuilder(StageTypeSync.class, id(), NetworkDirection.PLAY_TO_SERVER)
           .decoder(StageTypeSync::new)
           .encoder(StageTypeSync::encode)
           .consumer(StageTypeSync::handle)
           .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        if (player != null)  {
            //Don't send packet to offline player.
            //Parties.LOGGER.debug("Sending packet to player {} containing {}", player.getScoreboardName(), message.getClass().getName());
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
        }

    }

    public static <MSG> void sendToServer(MSG message) {
        //Parties.LOGGER.debug("Sending packet to server containing {}", message.getClass().getName());
        INSTANCE.sendToServer(message);
    }


}
