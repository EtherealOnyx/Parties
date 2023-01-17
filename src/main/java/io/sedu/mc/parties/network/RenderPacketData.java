package io.sedu.mc.parties.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class RenderPacketData {
    private UUID player;
    private int type;
    private Object data;

    RenderPacketData(FriendlyByteBuf buf) {
        this.type = buf.readInt();
        player = new UUID(buf.readLong(), buf.readLong());
        readData(buf);
    }

    private void readData(FriendlyByteBuf buf) {
        switch(type) {
            case 0: //Name
                StringBuilder builder = new StringBuilder();
                while (true) {
                    try {
                        builder.append(buf.readChar());
                    } catch(IndexOutOfBoundsException e) {
                        break;
                    }
                }
                data = builder.toString();
                break;

            case 1: //???
                break;
        }
    }

    private void writeData(FriendlyByteBuf buf) {
        switch(type) {
            case 0: //Name
                for (int letter : ((String)data).toCharArray()) {
                    buf.writeChar(letter);
                }
                break;

            case 1: //???
                break;
        }
    }

    public RenderPacketData(int type, UUID player, Object data) {
        System.out.println("Sending packet with TYPE : " + type);
        this.type = type;
        this.player = player;
        this.data = data;
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeLong(player.getMostSignificantBits());
        buf.writeLong(player.getLeastSignificantBits());
        writeData(buf);
    }

    boolean handle(Supplier<NetworkEvent.Context> context) {
        switch (type) {
            //Name
            case 0 -> RenderPacketHelper.setName(player, (String) data);
            default -> {
                return false;
            }
        }
        return true;
    }
}
