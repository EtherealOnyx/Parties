package eomods.combatoverhaul.eoparties.network;

import eomods.combatoverhaul.eoparties.data.client.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class HandlerData implements IMessageHandler<PacketData, IMessage> {

    //We are on client and received packet from server.
    @Override
    public IMessage onMessage(PacketData message, MessageContext ctx) {
        System.out.println("RECEIVED MESSAGE FROM SERVER OF TYPE : " + message.getType());
        switch(message.getType()) {

            //#0 - Sends a UUID to the client stating that the player is online.
            case 0:
                ClientData.changeOnline(message.getList().get(0), true);
                break;

            //#1 - Sends a UUID to the client stating that the player is offline.
            case 1:
                ClientData.changeOnline(message.getList().get(0), false);
                break;
            //#2 - Sends a list of UUIDs to the client, where all of them are player UUID's that need to be added to
            // the client's party list.
            case 2:
                ClientData.addMembers(message.getList());
                break;
            //#3 - Sends a list of UUIDs to the client, where the first one is a player UUID, and the rest are
            // its pets. This one tells the client to add pets.
            case 3:
                ClientData.addPetMembers(message.getList().get(0), (UUID[])(message.getList().subList(1,
                        message.getList().size()).toArray()));
                break;
            //#4 - Sends a list of UUIDs to the client, where the first one is a player UUID, and the rest are
            // its pets. This one tells the client to remove pets.
            case 4:
                ClientData.removePetMembers(message.getList().get(0), (UUID[])(message.getList().subList(1,
                        message.getList().size()).toArray()));
                break;
            //#5 - Sends a UUID to the client, to tell it that the specific UUID is now the party leader of the party.
            case 5:
                if (message.getList().size() == 0)
                    ClientData.changeLeader();
                else
                    ClientData.changeLeader(message.getList().get(0));
                break;
            //#6 - Sends a UUID to the client, to tell it that the specified UUID is no longer in the party.
            case 6:
                if (message.getList().size() == 0)
                    ClientData.removePartyMember();
                else
                    ClientData.removePartyMember(message.getList().get(0));
                break;
            //#7 - Sends a UUID to the client, to tell it to remove the specified UUID from client-side tracking.
            case 7:
                if (message.getList().size() == 0)
                    ClientData.moveAllTrackers();
                else
                    ClientData.removeTracker(message.getList().get(0));
                break;
            case 8:
                Minecraft.getMinecraft().addScheduledTask(ClientData::defaultData);
                break;
        }


        return null;
    }
}
