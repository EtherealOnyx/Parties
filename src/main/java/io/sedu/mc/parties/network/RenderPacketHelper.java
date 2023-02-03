package io.sedu.mc.parties.network;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class RenderPacketHelper {

    public static void setName(UUID player, String data) {
        if (ClientPlayerData.playerList.get(player).isTrackedOnServer()) {
            ClientPlayerData.playerList.get(player).setName(data);
            ClientPacketHelper.msgDebug("[" + player.getMostSignificantBits() + "] --> " + ClientPlayerData.getName(player));
        }
    }

    public static void setHealth(UUID player, float data) {
        if (ClientPlayerData.playerList.get(player).isTrackedOnServer()) {
            ClientPlayerData.playerList.get(player).setHealth(data);
            //TODO: Enable animation update when implemented
        }
    }

    public static void setAbsorb(UUID player, float data) {
        if (ClientPlayerData.playerList.get(player).isTrackedOnServer()) {
            ClientPlayerData.playerList.get(player).setAbsorb(data);
            //TODO: Enable animation update when implemented
        }
    }

    public static void setArmor(UUID player, int data) {
        if (ClientPlayerData.playerList.get(player).isTrackedOnServer()) {
            ClientPlayerData.playerList.get(player).setArmor(data);
            //TODO: Enable animation update when implemented
        }
    }

    public static void setFood(UUID player, int data) {
        ClientPlayerData.playerList.get(player).setFood(data);
        //TODO: Enable animation update when implemented
    }

    public static void setXp(UUID player, int data) {
        ClientPlayerData.playerList.get(player).setXp(data);
    }

    public static void setMaxHealth(UUID player, float datum) {
        ClientPlayerData.playerList.get(player).setMaxHealth(datum);
    }

    public static void markDeath(UUID player) {
        ClientPlayerData.playerList.get(player).markDead();
    }

    public static void markLife(UUID player) {
        ClientPlayerData.playerList.get(player).markAlive();
    }

    public static void setDim(UUID player, String data) {
        ClientPlayerData.playerList.get(player).dim.activate(data, false);
    }

    public static void markDeath() {
        if (ClientPlayerData.partySize() > 0) {
            markDeath(Minecraft.getInstance().player.getUUID());
        }
    }

    public static void markLife() {
        if (ClientPlayerData.partySize() > 0) {
            markLife(Minecraft.getInstance().player.getUUID());
            setDim(Minecraft.getInstance().player.getUUID(), String.valueOf(Minecraft.getInstance().level.dimension().location()));
        }
    }

    public static void addPotionEffect(UUID player, int type, int duration, int amp) {
        ClientPlayerData.playerList.get(player).addEffect(type, duration, amp);

    }

    public static void removePotionEffect(UUID player, int type) {
        ClientPlayerData.playerList.get(player).removeEffect(type);
    }
}
