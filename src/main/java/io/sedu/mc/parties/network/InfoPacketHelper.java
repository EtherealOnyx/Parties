package io.sedu.mc.parties.network;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;

import java.util.UUID;

public class InfoPacketHelper {

    public static void sendName(UUID sendTo, UUID nameOf) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), nameOf, 0, PlayerAPI.getName(nameOf));
    }

    public static void sendName(ServerPlayer sendTo, UUID nameOf) {
        sendData(sendTo, nameOf, 0, PlayerAPI.getName(nameOf));
    }

    public static void sendData(ServerPlayer sendTo, UUID propOf, int type, Object data) {
        if (sendTo == null)
            return;
        if (!sendTo.getUUID().equals(propOf))
            PartiesPacketHandler.sendToPlayer(new RenderPacketData(type, propOf, data), sendTo);
    }

    public static void sendHealth(UUID sendTo, UUID healthOf, float health) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), healthOf, 1, health);
    }

    public static void sendMaxHealth(UUID sendTo, UUID absorbOf, float max) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), absorbOf, 2, max);
    }

    public static void sendAbsorb(UUID sendTo, UUID absorbOf, float absorb) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), absorbOf, 3, absorb);
    }

    public static void sendArmor(UUID sendTo, UUID armorOf, int armorValue) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), armorOf, 4, armorValue);
    }

    public static void sendFood(UUID sendTo, UUID hungerOf, int foodLevel) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), hungerOf, 5, foodLevel);
    }

    public static void sendXp(UUID sendTo, UUID levelOf, int levels) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), levelOf, 6, levels);
    }

    public static void forceUpdate(UUID sendTo, UUID propOf, boolean withDim) {
        if (sendTo.equals(propOf))
            return;
        ServerPlayer p;
        if ((p = PlayerAPI.getNormalServerPlayer(propOf)) != null) {
            InfoPacketHelper.sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, p.getHealth(), p.getMaxHealth(), p.getAbsorptionAmount(), p.getArmorValue(), p.getFoodData().getFoodLevel(), p.experienceLevel);
            if (withDim)
                InfoPacketHelper.sendDim(sendTo, propOf, p.level.dimension().location());
            if (p.isDeadOrDying())
                InfoPacketHelper.sendDeath(sendTo, propOf);
            else
                p.getActiveEffects().forEach(effect -> sendEffect(sendTo, propOf, MobEffect.getId(effect.getEffect()), effect.getDuration(), effect.getAmplifier()));
        }
    }



    private static void sendData(ServerPlayer serverPlayer, UUID propOf, float health, float maxHealth, float absorptionAmount, int armorValue, int foodLevel, int experienceLevel) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(-1, propOf, health, maxHealth, absorptionAmount, armorValue, foodLevel, experienceLevel), serverPlayer);
    }

    public static void sendDeath(UUID sendTo, UUID propOf) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(7, propOf), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendAlive(UUID sendTo, UUID propOf) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(8, propOf), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendDim(UUID sendTo, UUID propOf, ResourceLocation world) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(propOf, world), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendDeath(ServerPlayer p) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(10), p);
    }

    public static void sendLife(ServerPlayer p) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(11), p);
    }

    public static void sendEffectExpired(UUID sendTo, UUID propOf, int potionEffect) {

        PartiesPacketHandler.sendToPlayer(new RenderPacketData(13, propOf, potionEffect), PlayerAPI.getNormalServerPlayer(sendTo));

    }

    public static void sendEffect(UUID sendTo, UUID propOf, int type, int duration, int amp) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(12, propOf, type, duration, amp), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendEffectExpired(UUID sendTo, int id) {
        sendEffectExpired(sendTo, sendTo, id);
    }

    public static void sendEffect(UUID sendTo, int id, int duration, int amplifier) {
        sendEffect(sendTo, sendTo, id, duration, amplifier);
    }

    public static void sendXpBar(UUID sendTo, UUID propOf, float bar) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 14, bar);
    }

    public static void sendClose(ServerPlayer p) {
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(8), p);
    }

    public static void sendBleeding(ServerPlayer p, boolean b, int dur) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(15, p.getUUID(), b, dur), p);
    }

    public static void sendBleeding(UUID sendTo, UUID propOf, boolean b, int dur) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(15, propOf, b, dur), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendDowned(ServerPlayer p, boolean b, int dur) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(17, p.getUUID(), b, dur), p);
    }

    public static void sendDowned(UUID sendTo, UUID propOf, boolean b, int dur) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(17, propOf, b, dur), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendReviveUpdate(UUID sendTo, UUID propOf, float revive) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(16, propOf, revive), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendReviveUpdate(UUID sendTo, float revive) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(16, sendTo, revive), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendSpectating(UUID sendTo, UUID propOf, boolean b) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(18, propOf, b), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendSpectating(UUID sendTo, boolean b) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(18, sendTo, b), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendThirstUpdate(UUID sendTo, UUID propOf, int thirst) {
            sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 19, thirst);
    }

    public static void sendWorldTempUpdate(UUID sendTo, UUID propOf, float worldTemp) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 20, worldTemp);
    }

    public static void sendBodyTempUpdate(UUID sendTo, UUID propOf, float bodyTemp) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 21, bodyTemp);
    }

    public static void sendWorldTempUpdateTAN(UUID sendTo, UUID propOf, float worldTemp) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 22, worldTemp);
    }

    public static void sendManaUpdate(UUID sendTo, UUID propOf, float mana) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(23, propOf, mana), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendMaxManaUpdate(UUID sendTo, UUID propOf, int maxMana) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 24, maxMana);
    }

    public static void sendStaminaUpdate(UUID sendTo, UUID propOf, float currentStamina) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 25, currentStamina);
    }

    public static void sendMaxStaminaUpdate(UUID sendTo, UUID propOf, int maxStamina) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 26, maxStamina);
    }

    public static void sendManaUpdateSS(UUID sendTo, UUID propOf, float curMana) {
        Parties.LOGGER.debug("Sending mana update:" + curMana);
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 27, curMana);
    }

    public static void sendMaxManaUpdateSS(UUID sendTo, UUID propOf, float mana) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 28, mana);
    }

    public static void sendExtraManaUpdateSS(UUID sendTo, UUID propOf, float mana) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 29, mana);
    }

    public static void sendExtraStamUpdate(UUID sendTo, UUID propOf, int extra) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 30, extra);
    }

    public static void sendQuenchUpdate(UUID sendTo, UUID propOf, int quench) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 31, quench);
    }

    public static void sendMaxHungerUpdate(UUID sendTo, UUID propOf, float max) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 32, max);
    }

    public static void sendSaturationUpdate(ServerPlayer player, float saturation) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(33, player.getUUID(), saturation), player);
    }

    public static void sendSaturationUpdate(UUID sendTo, UUID propOf, float sat) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 33, sat);
    }

    public static void sendOriginUpdate(UUID sendTo, UUID propOf, String origin) {
        sendData(PlayerAPI.getNormalServerPlayer(sendTo), propOf, 34, origin);
    }

    public static void sendOriginUpdate(UUID sendTo, String origin) {
        PartiesPacketHandler.sendToPlayer(new RenderPacketData(34, sendTo, origin), PlayerAPI.getNormalServerPlayer(sendTo));
    }

    public static void sendManaUpdateI(UUID id, UUID propOf, Integer cur) {
        sendData(PlayerAPI.getNormalServerPlayer(id), propOf, 35, cur);
    }

    public static void sendMaxManaUpdateI(UUID id, UUID propOf, Integer max) {
        sendData(PlayerAPI.getNormalServerPlayer(id), propOf, 36, max);
    }
}
