package eomods.combatoverhaul.eoparties.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class PacketData implements IMessage {

    private ArrayList<UUID> listofIDs;
    private int type;

    public PacketData(int type, ArrayList<UUID> list) {
        this.type = type;
        this.listofIDs = list;
    }

    public PacketData() {
        type = -1;
        listofIDs = new ArrayList<>();
    }

    public PacketData(int type, UUID... id) {
        this.type = type;
        this.listofIDs = new ArrayList<>();
        listofIDs.addAll(Arrays.asList(id));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type);
        for (UUID id : listofIDs) {
            buf.writeLong(id.getMostSignificantBits());
            buf.writeLong(id.getLeastSignificantBits());
        }

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = buf.readInt();
        listofIDs = new ArrayList<>();
        while (true) {
            try {
                listofIDs.add(new UUID(buf.readLong(), buf.readLong()));
            } catch(IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    int getType() {
        return type;
    }

    ArrayList<UUID> getList() {
        return listofIDs;
    }
}
