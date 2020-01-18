package eomods.combatoverhaul.eoparties.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientPacketEntity {
    private UUID id;
    private int type;
    private float amount;

    public ClientPacketEntity(UUID id, int type, float amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
    }

    void encode(PacketBuffer buf) {
        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());
        buf.writeInt(type);
        buf.writeFloat(amount);
    }

    ClientPacketEntity(PacketBuffer buf) {
        id = new UUID(buf.readLong(), buf.readLong());
        type = buf.readInt();
        amount = buf.readFloat();
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        //Type = Entity Update Type.
        //Wait maybe you don't need a switch statement here.
        switch(type) {

            //#0 - Max Health
            case 0:
                break;
            //#1 - Current Health
            case 1:
                break;
            //#2 - Current Hunger
            case 2:
                break;
            //#3 - Current Saturation
            case 3:
                break;
            //#4 - Current Armor
            case 4:
                break;
            //#5 - Mark entity as dead.
            case 5:
                break;
        }
    }

}
