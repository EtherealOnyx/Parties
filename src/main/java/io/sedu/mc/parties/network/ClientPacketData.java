package io.sedu.mc.parties.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class ClientPacketData {

    private ArrayList<UUID> list;
    private int type;

    ClientPacketData(FriendlyByteBuf buf) {
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

    void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        for (UUID id : list) {
            buf.writeLong(id.getMostSignificantBits());
            buf.writeLong(id.getLeastSignificantBits());
        }

    }

    void handle(Supplier<NetworkEvent.Context> context) {
        switch(type) {
            //#0 - Sends a UUID to the client stating that the player is online.
            case 0:
                PacketHelper.markOnline(list);
                break;
            //#1 - Sends a UUID to the client stating that the player is offline.
            case 1:
                PacketHelper.markOffline(list);
                break;
            //#2 - Sends a list of UUIDs to the client, where all of them are player UUID's that need to be added to
            // the client's party list.
            case 2:
                PacketHelper.addMembers(list);
                break;
            //#3 - Sends a UUID to the client, to tell it that the specific UUID is now the party leader of the party.
            case 3:
                PacketHelper.changeLeader(list);
                break;
            //#4 - Sends a UUID to the client, to tell it that the specified UUID is no longer in the party.
            case 4:
                if (list.size() == 0)
                    PacketHelper.dropParty();
                else
                    PacketHelper.removePartyMemberDropped(list.get(0));
                break;
            //#5 Sends a packet to the client, indicating that the player has been kicked from the party.
            case 5:
                if (list.size() == 0)
                    PacketHelper.dropPartyKicked();
                else
                    PacketHelper.removePartyMemberKicked(list.get(0));
                break;
            //#6 Sends a packet to the client, indicating that the party has been disbanded.
            case 6:
                PacketHelper.disbandParty();
                break;
        }
    }
}
