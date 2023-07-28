package io.sedu.mc.parties.network;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;

import java.util.UUID;

import static io.sedu.mc.parties.api.helper.PlayerAPI.getClientPlayer;


public class RenderPacketHelper {

    public static void setName(UUID player, String data) {
        getClientPlayer(player, p -> {
            if (p.isTrackedOnServer()) {
                p.setName(data);
                ClientPacketHelper.msgDebug("[" + player.getMostSignificantBits() + "] --> " + ClientPlayerData.getName(player));
            }
        });
    }

    public static void setHealth(UUID player, float data) {
        getClientPlayer(player, p -> {
            if (p.isTrackedOnServer()) {
                p.setHealth(data);
            }
        });
    }

    public static void setAbsorb(UUID player, float data) {
        getClientPlayer(player, p -> {
            if (p.isTrackedOnServer()) {
                p.setAbsorb(data);
            }
        });
    }

    public static void setArmor(UUID player, int data) {
        getClientPlayer(player, p -> {
            if (p.isTrackedOnServer()) {
                p.setArmor(data);
            }
        });
    }

    public static void setFood(UUID player, int data) {
        getClientPlayer(player, p -> p.setFood(data));
    }

    public static void setXp(UUID player, int data) {
        getClientPlayer(player, p -> p.setXp(data));
    }

    public static void setMaxHealth(UUID player, float datum) {
        getClientPlayer(player, p -> p.setMaxHealth(datum));
    }

    public static void markDeath(UUID player) {
        getClientPlayer(player, ClientPlayerData::markDead);
    }

    public static void markLife(UUID player) {
        getClientPlayer(player, ClientPlayerData::markAlive);
    }

    public static void setDim(UUID player, String data) {
        if (RenderItem.items.get("dim").isEnabled()) getClientPlayer(player, p -> p.getDim().activate(data, false));
    }

    public static void markDeath() {
        ClientPlayerData.getSelf(ClientPlayerData::markDead);
    }

    public static void markLife() {
        ClientPlayerData.getSelf(player -> {
            player.markAlive();
            if (RenderItem.items.get("dim").isEnabled() && Minecraft.getInstance().level != null)  player.getDim().activate(String.valueOf(Minecraft.getInstance().level.dimension().location()), false);
        });
    }

    public static void addPotionEffect(UUID player, int type, int duration, int amp) {
        getClientPlayer(player, p -> p.addEffect(type, duration, amp));
    }

    public static void removePotionEffect(UUID player, int type) {
        getClientPlayer(player, p -> p.removeEffect(type));
    }

    public static void setXpBar(UUID player, Float data) {
        getClientPlayer(player, p -> p.setXpBar(data));
    }

    public static void setBleeding(UUID player, boolean isBleeding, Integer datum) {
        getClientPlayer(player, p -> p.changeBleeding(isBleeding, datum));
    }

    public static void setDowned(UUID player, boolean isDowned, Integer datum) {
        getClientPlayer(player, p -> p.changeDownedState(isDowned, datum));
    }

    public static void setReviveProgress(UUID player, Float data) {
        getClientPlayer(player, p -> p.setReviveProgress(data));
    }

    public static void setSpectating(UUID player, Boolean data) {
        getClientPlayer(player, p -> p.setSpectator(data));
    }

    public static void setThirst(UUID player, Integer data) {
        getClientPlayer(player, p -> p.setThirst(data));
    }

    public static void setWorldTemp(UUID player, Float data) {
        getClientPlayer(player, p -> p.setWorldTemp(data));
    }

    public static void setBodyTemp(UUID player, Float data) {
        getClientPlayer(player, p -> p.setBodyTemp(data));
    }

    public static void setWorldTempTAN(UUID player, float data) {
        getClientPlayer(player, p -> p.setWorldTempTAN((int) data));
    }

    public static void setMana(UUID player, float data) {
        getClientPlayer(player, p -> p.setMana(data));
    }

    public static void setMaxMana(UUID player, int data) {
        getClientPlayer(player, p -> p.setMaxMana(data));
    }

    public static void setCurrentStamina(UUID player, Float data) {
        getClientPlayer(player, p -> p.setCurrentStamina(data));
    }

    public static void setMaxStamina(UUID player, Integer data) {
        Parties.LOGGER.debug("Setting max stamina..." + data);
        getClientPlayer(player, p -> p.setMaxStamina(data));
    }

    public static void setManaSS(UUID player, Float data) {
        getClientPlayer(player, p -> p.setManaSS(data));
    }

    public static void setMaxManaSS(UUID player, Float data) {
        getClientPlayer(player, p -> p.setMaxManaSS(data));
    }

    public static void setExtraMana(UUID player, Float data) {
        getClientPlayer(player, p -> p.setExtraManaSS(data));
    }


    public static void setExtraStam(UUID player, Integer data) {
        Parties.LOGGER.debug("Setting extra stamina...");
        getClientPlayer(player, p -> p.setExtraStam(data));
    }

    public static void setQuench(UUID player, Integer data) {
        Parties.LOGGER.debug("Setting quench level...");
        getClientPlayer(player, p -> p.setQuench(data));
    }

    public static void setMaxHunger(UUID player, Float data) {
        getClientPlayer(player, p -> p.setMaxHunger(data));
    }

    public static void setSaturation(UUID player, float data) {
        getClientPlayer(player, p -> p.setSaturation(data));
    }

    public static void setOrigin(UUID player, String data) {
        getClientPlayer(player, p -> p.setOrigin(data));
    }

    public static void setManaI(UUID player, Integer data) {
        getClientPlayer(player, p -> p.setManaI(data));
    }

    public static void setMaxManaI(UUID player, Integer data) {
        getClientPlayer(player, p -> p.setMaxManaI(data));
    }

    public static void startCast(UUID player, Integer spellId, Integer castTime) {
        getClientPlayer(player, playerData -> playerData.initSpell(spellId, castTime));
    }

    public static void endCast(UUID player) {
        getClientPlayer(player, ClientPlayerData::finishSpell);
    }
}
