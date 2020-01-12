package eomods.combatoverhaul.eoparties.network;

import eomods.combatoverhaul.eoparties.data.client.ClientData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientPacketName {
    private UUID id;
    private String name;

    public ClientPacketName(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    void encode(PacketBuffer buf) {
        buf.writeLong(id.getMostSignificantBits());
        buf.writeLong(id.getLeastSignificantBits());
        for (int letter : name.toCharArray()) {
            buf.writeChar(letter);
        }
    }

    ClientPacketName(PacketBuffer buf) {
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

    void handle(Supplier<NetworkEvent.Context> context) {
        ClientData.changeName(id, name);
    }
}
