package eomods.combatoverhaul.eoparties.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class PacketName implements IMessage {
    private UUID id;
    private String name;

    public PacketName(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public PacketName() {
        id = null;
        name = null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());
        for (int letter : name.toCharArray()) {
            buf.writeChar(letter);
        }

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = new UUID(buf.readLong(), buf.readLong());
        StringBuilder builder = new StringBuilder();
        while (true) {
            try {
                builder.append(buf.readChar());
                System.out.println(id.toString() + builder.toString());
            } catch(IndexOutOfBoundsException e) {
                break;
            }
        }
        name = builder.toString();
    }

    String getName() {
        return name;
    }

    UUID getId() {
        return id;
    }
}
