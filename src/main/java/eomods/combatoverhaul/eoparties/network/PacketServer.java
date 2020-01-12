package eomods.combatoverhaul.eoparties.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class PacketServer implements IMessage {
    private UUID id;
    private int type;

    public PacketServer(int type, UUID id) {
        this.id = id;
        this.type = type;
    }

    public PacketServer() {
        id = null;
        type = -1;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        System.out.println("SENDING SERVER MESSAGE OF TYPE : " + type);
        buf.writeInt(type);
        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readInt();
        id = new UUID(buf.readLong(), buf.readLong());
    }

    int getType() {
        return type;
    }

    UUID getId() {
        return id;
    }
}
