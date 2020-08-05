package com.github.etherealonyx.parties.network;

import com.github.etherealonyx.parties.data.client.PacketHelper;
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

    public ClientPacketData(int type, ArrayList<UUID> list, UUID idToRemove) {
        this.type = type;
        this.list = new ArrayList<>(list);
        this.list.removeIf(uuid -> uuid.equals(idToRemove));
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
                PacketHelper.markOnline(list.get(0));
                break;

            //#1 - Sends a UUID to the client stating that the player is offline.
            case 1:
                PacketHelper.markOffline(list.get(0));
                break;
            //#2 - Sends a list of UUIDs to the client, where all of them are player UUID's that need to be added to
            // the client's party list.
            case 2:
                PacketHelper.addMembers(list);
                break;
            //#3 - Sends a list of UUIDs to the client, where the first one is a player UUID, and the rest are
            // its pets. This one tells the client to add pets.
            case 3:
                PacketHelper.addPetMembers(list.get(0), list.subList(1,
                        list.size()));
                break;
            //#4 - Sends a list of UUIDs to the client, where the first one is a player UUID, and the rest are
            // its pets. This one tells the client to remove pets.
            case 4:
                PacketHelper.removePetMembers(list.get(0), list.subList(1,
                        list.size()));
                break;
            //#5 - Sends a UUID to the client, to tell it that the specific UUID is now the party leader of the party.
            case 5:
                if (list.size() == 0)
                    PacketHelper.changeLeader();
                else
                    PacketHelper.changeLeader(list.get(0));
                break;
            //#6 - Sends a UUID to the client, to tell it that the specified UUID is no longer in the party.
            case 6:
                if (list.size() == 0)
                    PacketHelper.dropParty();
                else
                    PacketHelper.removePartyMemberDropped(list.get(0));
                break;
            //#7 - Sends a UUID to the client, to tell it to remove the specified UUID from client-side tracking.
            case 7:
                if (list.size() == 0)
                    PacketHelper.moveAllTrackers();
                else
                    PacketHelper.moveTracker(list.get(0));
                break;
            //#8 - Sends a packet to the client, to tell it to load default data, that's when they store their own
            // player information in the party list.
            case 8:
                PacketHelper.defaultData();
                break;
            //Sends a packet to the client, indicating that the player has been kicked from the party.
            case 9:
                if (list.size() == 0)
                    PacketHelper.dropPartyKicked();
                else
                    PacketHelper.removePartyMemberKicked(list.get(0));
                break;
            //Sends a packet to the client, indicating that the party has been disbanded.
            case 10:
                PacketHelper.disbandParty();
                break;
            case 11:
                //Sends a packet to the client, indicating that the client should attempt to update their trackers on
                // the client side.
                PacketHelper.attemptToFindTrackers();
        }
    }
}
