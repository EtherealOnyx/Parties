package eomods.combatoverhaul.eoparties.network;

import eomods.combatoverhaul.eoparties.data.server.Events;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ServerPacketData {
    private UUID id;
    private int type;

    public ServerPacketData(int type, UUID id) {
        this.id = id;
        this.type = type;
    }

    void encode(PacketBuffer buf) {
        System.out.println("SENDING SERVER MESSAGE OF TYPE : " + type);
        buf.writeInt(type);
        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());
    }

    ServerPacketData(PacketBuffer buf) {
        type = buf.readInt();
        id = new UUID(buf.readLong(), buf.readLong());
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        System.out.println("RECEIVED PACKET OF TYPE : " + type);
        switch(type) {
            //#0 - Sends a UUID to the server to tell them the client is tracking it now.
            case 0:
                Events.moveToClient(context.get().getSender().getUniqueID(),id);
                break;

            //#1 - Sends a UUID to the server stating that the client requests the server to track it.
            case 1:
                if (Events.validatePartyMember(context.get().getSender().getUniqueID(), id))
                    Events.moveToServer(context.get().getSender().getUniqueID(), id);
                break;
        }
    }
}
