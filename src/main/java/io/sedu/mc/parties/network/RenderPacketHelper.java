package io.sedu.mc.parties.network;

import io.sedu.mc.parties.client.ClientPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

import java.util.UUID;

public class RenderPacketHelper {
    private static void msg(String msg) {
        Minecraft.getInstance().player.sendMessage(new TextComponent(msg), Minecraft.getInstance().player.getUUID());
    }
    public static void setName(UUID player, String data) {
        if (ClientPlayerData.playerList.get(player).isTrackedOnServer()) {
            ClientPlayerData.playerList.get(player).setName(data);
            msg("[" + player.getMostSignificantBits() + "] --> " + ClientPlayerData.getName(player));
        }
    }
}
