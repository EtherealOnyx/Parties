package io.sedu.mc.parties.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class ServerPacketData {

    private UUID data;
    private final int type;

    public ServerPacketData(FriendlyByteBuf buf) {
        this.type = buf.readInt();
        try {
            data = new UUID(buf.readLong(), buf.readLong());
        } catch(IndexOutOfBoundsException ignored) {}
    }

    public ServerPacketData(int i, UUID playerToTrack) {
        data = playerToTrack;
        this.type = i;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeLong(data.getMostSignificantBits());
        buf.writeLong(data.getLeastSignificantBits());
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            switch (type) {
                case 0 -> //Send UUID to server to tell them the client is tracking it now.
                        ServerPacketHelper.trackerToClient(ctx.getSender().getUUID(), data);
                case 1 -> //Send UUID to server to tell them the client requests the server to track it now.
                        ServerPacketHelper.trackerToServer(ctx.getSender().getUUID(), data);
            }
        });
        return true;
    }


}
