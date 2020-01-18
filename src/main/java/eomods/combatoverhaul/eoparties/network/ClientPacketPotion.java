package eomods.combatoverhaul.eoparties.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientPacketPotion {
    private UUID id;
    private int type;
    private int duration;

    public ClientPacketPotion(UUID id, int type, int duration) {
        this.id = id;
        this.type = type;
        this.duration = duration;
    }

    void encode(PacketBuffer buf) {
        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());
        buf.writeInt(type);
        buf.writeInt(duration);
    }

    ClientPacketPotion(PacketBuffer buf) {
        id = new UUID(buf.readLong(), buf.readLong());
        type = buf.readInt();
        duration = buf.readInt();
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        //Type = Entity Potion Type
        //Call StatUpdateRenderHelper or whatever for a Potion Update.
        //If duration == 0 then the potion is to be removed.
    }

}
