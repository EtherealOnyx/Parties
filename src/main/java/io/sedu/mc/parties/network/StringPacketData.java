package io.sedu.mc.parties.network;

import io.sedu.mc.parties.Parties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StringPacketData {

    private String data;
    private final int type;

    public StringPacketData(FriendlyByteBuf buf) {
        this.type = buf.readInt();
        try {
            data = buf.readUtf();
        } catch(IndexOutOfBoundsException ignored) {}
    }

    public StringPacketData(int i, String encoded) {
        data = encoded;
        this.type = i;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeUtf(data);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            switch (type) {
                case 0 -> { //Send UUID to server to tell them the client is tracking it now.
                    if (stringValidator(data)) {
                        ServerPacketHelper.sendMessageToAll(ctx.getSender().server.getPlayerList().getPlayers(), ctx.getSender(), data);
                    } else {
                        Parties.LOGGER.warn("[Parties] [Link to Chat] Preset validation failed for {}.", ctx.getSender().getName().getContents());
                    }

                }


            }
        });
        return true;
    }

    private boolean stringValidator(String data) {
        return data.matches("^[A-Za-z0-9+/\\-=:|]+");
    }


}
