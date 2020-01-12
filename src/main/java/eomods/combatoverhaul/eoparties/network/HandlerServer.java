package eomods.combatoverhaul.eoparties.network;

import eomods.combatoverhaul.eoparties.data.server.Events;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerServer implements IMessageHandler<PacketServer, IMessage> {

    //We are on server and received packet from client.
    @Override
    public IMessage onMessage(PacketServer message, MessageContext ctx) {
        System.out.println("RECEIVED PACKET OF TYPE : " + message.getType());
        switch(message.getType()) {
            //#0 - Sends a UUID to the server to tell them the client is tracking it now.
            case 0:
                Events.moveToClient(ctx.getServerHandler().player.getUniqueID(), message.getId());
                break;

            //#1 - Sends a UUID to the server stating that the client requests the server to track it.
            case 1:
                if (Events.validatePartyMember(ctx.getServerHandler().player.getUniqueID(), message.getId()))
                    Events.moveToServer(ctx.getServerHandler().player.getUniqueID(), message.getId());
                break;
        }

        return null;
    }
}
