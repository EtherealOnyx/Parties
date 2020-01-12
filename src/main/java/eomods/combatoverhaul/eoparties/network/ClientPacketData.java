package eomods.combatoverhaul.eoparties.network;

import eomods.combatoverhaul.eoparties.data.client.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

public class ClientPacketData {
    private ArrayList<UUID> list;
    private int type;

    public ClientPacketData(int type, ArrayList<UUID> list) {
        this.type = type;
        this.list = list;
    }

    public ClientPacketData(int type, UUID... id) {
        this.type = type;
        this.list = new ArrayList<>();
        list.addAll(Arrays.asList(id));
    }

    void encode(PacketBuffer buf) {
        buf.writeInt(type);
        for (UUID id : list) {
            buf.writeLong(id.getMostSignificantBits());
            buf.writeLong(id.getLeastSignificantBits());
        }

    }

    ClientPacketData(PacketBuffer buf) {
        this.type = buf.readInt();
        list = new ArrayList<>();
        while (true) {
            try {
                list.add(new UUID(buf.readLong(), buf.readLong()));
            } catch(IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        switch(type) {

            //#0 - Sends a UUID to the client stating that the player is online.
            case 0:
                ClientData.changeOnline(list.get(0), true);
                break;

            //#1 - Sends a UUID to the client stating that the player is offline.
            case 1:
                ClientData.changeOnline(list.get(0), false);
                break;
            //#2 - Sends a list of UUIDs to the client, where all of them are player UUID's that need to be added to
            // the client's party list.
            case 2:
                ClientData.addMembers(list);
                break;
            //#3 - Sends a list of UUIDs to the client, where the first one is a player UUID, and the rest are
            // its pets. This one tells the client to add pets.
            case 3:
                ClientData.addPetMembers(list.get(0), (UUID[])(list.subList(1,
                        list.size()).toArray()));
                break;
            //#4 - Sends a list of UUIDs to the client, where the first one is a player UUID, and the rest are
            // its pets. This one tells the client to remove pets.
            case 4:
                ClientData.removePetMembers(list.get(0), (UUID[])(list.subList(1,
                        list.size()).toArray()));
                break;
            //#5 - Sends a UUID to the client, to tell it that the specific UUID is now the party leader of the party.
            case 5:
                if (list.size() == 0)
                    ClientData.changeLeader();
                else
                    ClientData.changeLeader(list.get(0));
                break;
            //#6 - Sends a UUID to the client, to tell it that the specified UUID is no longer in the party.
            case 6:
                if (list.size() == 0)
                    ClientData.dropParty();
                else
                    ClientData.removePartyMember(list.get(0));
                break;
            //#7 - Sends a UUID to the client, to tell it to remove the specified UUID from client-side tracking.
            case 7:
                if (list.size() == 0)
                    ClientData.moveAllTrackers();
                else
                    ClientData.removeTracker(list.get(0));
                break;
            case 8:
                Minecraft.getInstance().deferTask(ClientData::defaultData);
                break;
        }
    }

}
