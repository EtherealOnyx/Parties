package io.sedu.mc.parties.network;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.RenderItem;
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
        }
    }

    public static void setAbsorb(UUID player, float data) {
        if (ClientPlayerData.playerList.get(player).isTrackedOnServer()) {
            ClientPlayerData.playerList.get(player).setAbsorb(data);
        }
    }

    public static void setArmor(UUID player, int data) {
        if (ClientPlayerData.playerList.get(player).isTrackedOnServer()) {
            ClientPlayerData.playerList.get(player).setArmor(data);
        }
    }

    public static void setFood(UUID player, int data) {
        ClientPlayerData.playerList.get(player).setFood(data);
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
        if (RenderItem.items.get("dim").isEnabled()) ClientPlayerData.playerList.get(player).getDim().activate(data, false);
    }

    public static void markDeath() {
        if (ClientPlayerData.partySize() > 0) {
            assert Minecraft.getInstance().player != null;
            markDeath(Minecraft.getInstance().player.getUUID());
        }
    }

    public static void markLife() {
        if (ClientPlayerData.partySize() > 0) {
            assert Minecraft.getInstance().player != null;
            markLife(Minecraft.getInstance().player.getUUID());
            assert Minecraft.getInstance().level != null;
            setDim(Minecraft.getInstance().player.getUUID(), String.valueOf(Minecraft.getInstance().level.dimension().location()));
        }
    }

    public static void addPotionEffect(UUID player, int type, int duration, int amp) {
        ClientPlayerData.playerList.get(player).addEffect(type, duration, amp);

    }

    public static void removePotionEffect(UUID player, int type) {
        ClientPlayerData.playerList.get(player).removeEffect(type);
    }

    public static void setXpBar(UUID player, Float data) {
        ClientPlayerData.playerList.get(player).setXpBar(data);
    }

    public static void setBleeding(UUID player, boolean isBleeding, Integer datum) {
        ClientPlayerData.playerList.get(player).changeBleeding(isBleeding, datum);
    }

    public static void setDowned(UUID player, boolean isDowned, Integer datum) {
        ClientPlayerData.playerList.get(player).changeDownedState(isDowned, datum);
    }

    public static void setReviveProgress(UUID player, Float data) {
        ClientPlayerData.playerList.get(player).setReviveProgress(data);
    }

    public static void setSpectating(UUID player, Boolean data) {
        ClientPlayerData.playerList.get(player).setSpectator(data);
    }

    public static void setThirst(UUID player, Integer data) {
        ClientPlayerData.playerList.get(player).setThirst(data);
    }

    public static void setWorldTemp(UUID player, Float data) {
        ClientPlayerData.playerList.get(player).setWorldTemp(data);
    }

    public static void setBodyTemp(UUID player, Float data) {
        ClientPlayerData.playerList.get(player).setBodyTemp(data);
    }

    public static void setWorldTempTAN(UUID player, float data) {
        ClientPlayerData.playerList.get(player).setWorldTempTAN((int)data);
    }

    public static void setMana(UUID player, float data) {
        ClientPlayerData.playerList.get(player).setMana(data);
    }

    public static void setMaxMana(UUID player, int data) {
        ClientPlayerData.playerList.get(player).setMaxMana(data);
    }

    public static void setCurrentStamina(UUID player, Float data) {
        ClientPlayerData.playerList.get(player).setCurrentStamina(data);
    }

    public static void setMaxStamina(UUID player, Integer data) {
        ClientPlayerData.playerList.get(player).setMaxStamina(data);
    }
}
