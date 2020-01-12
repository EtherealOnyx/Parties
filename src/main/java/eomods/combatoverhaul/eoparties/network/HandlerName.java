package eomods.combatoverhaul.eoparties.network;

import eomods.combatoverhaul.eoparties.data.client.ClientData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerName implements IMessageHandler<PacketName, IMessage> {

    //We are on client and received packet from server.
    @Override
    public IMessage onMessage(PacketName message, MessageContext ctx) {
        ClientData.changeName(message.getId(), message.getName());

        return null;
    }
}
