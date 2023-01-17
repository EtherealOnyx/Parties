package io.sedu.mc.parties.network;

import java.util.UUID;

import static io.sedu.mc.parties.data.Util.getName;
import static io.sedu.mc.parties.data.Util.getServerPlayer;

public class InfoPacketHelper {

    public static void sendName(UUID sendTo, UUID nameOf) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(0, nameOf, getName(nameOf)), getServerPlayer(sendTo));
    }
}
