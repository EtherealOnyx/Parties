package io.sedu.mc.parties.api.mod;

import io.sedu.mc.parties.api.mod.arsnoveau.IANHandler;
import io.sedu.mc.parties.api.mod.coldsweat.ICSHandler;
import io.sedu.mc.parties.api.mod.dietarystats.IDSHandler;
import io.sedu.mc.parties.api.mod.epicfight.IEFHandler;
import io.sedu.mc.parties.api.mod.feathers.IFHandler;
import io.sedu.mc.parties.api.mod.hardcorerevival.IHRHandler;
import io.sedu.mc.parties.api.mod.homeostatic.IHHandler;
import io.sedu.mc.parties.api.mod.incapacitated.IIHandler;
import io.sedu.mc.parties.api.mod.ironspellbooks.IISSHandler;
import io.sedu.mc.parties.api.mod.ironspellbooks.SpellHolder;
import io.sedu.mc.parties.api.mod.openpac.IPACHandler;
import io.sedu.mc.parties.api.mod.origins.IOHandler;
import io.sedu.mc.parties.api.mod.playerrevive.IPRHandler;
import io.sedu.mc.parties.api.mod.spellsandshields.ISSHandler;
import io.sedu.mc.parties.api.mod.tfc.ITFCHandler;
import io.sedu.mc.parties.api.mod.thirstmod.ITMHandler;
import io.sedu.mc.parties.api.mod.toughasnails.ITANHandler;
import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FakeHandler implements IANHandler, ICSHandler, IDSHandler, IEFHandler, IFHandler, IHRHandler, IHHandler, IIHandler, IISSHandler, IPACHandler, IOHandler, IPRHandler, ISSHandler, ITMHandler, ITANHandler, ITFCHandler {
    public static final FakeHandler INST = new FakeHandler();

    @Override
    public float getCurrentMana(Player player) {
        return 100;
    }

    @Override
    public int getMaxMana(Player player) {
        return 100;
    }

    @Override
    public float getWorldTemp(Player player) {
        return 0;
    }

    @Override
    public float getBodyTemp(Player player) {
        return 0;
    }

    @Override
    public void convertTemp(float worldTemp, BiConsumer<Integer, Integer> action) {

    }

    @Override
    public void getClientWorldTemp(Player clientPlayer, TriConsumer<Integer, Integer, Integer> action) {

    }

    @Override
    public int getWaterLevel(Player player) {
        return 20;
    }

    @Override
    public void getClientTemperature(Player clientPlayer, TriConsumer<Integer, Integer, Integer> action) {

    }

    @Override
    public void getTemperature(Player player, BiConsumer<Float, Float> action) {

    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public int getBodyTempSev(float data, int severity) {
        return 0;
    }

    @Override
    public int getWorldTempSev(float data, int severity) {
        return 0;
    }

    @Override
    public int convertTemp(float data) {
        return 0;
    }

    @Override
    public void setTempRender(Boolean renderTemp) {

    }

    @Override
    public void getManaValues(Player player, BiConsumer<Float, Integer> action) {

    }

    @Override
    public void getAllMana(Player player, TriConsumer<Float, Float, Float> action) {

    }

    @Override
    public float getMax(Player player) {
        return 100;
    }

    @Override
    public void setManaRender(Boolean renderMana) {

    }

    @Override
    public void getMaxHunger(Player p, Consumer<Float> action) {

    }

    @Override
    public void getClientValues(Player player, BiConsumer<Float, Integer> action) {

    }

    @Override
    public void getClientFeathers(TriConsumer<Integer, Integer, Integer> action) {

    }

    @Override
    public void getServerFeathers(ServerPlayer player, TriConsumer<Integer, Integer, Integer> action) {

    }

    @Override
    public void setFeathersRender(Boolean aBoolean) {

    }

    @Override
    public void getDowned(Player player, BiConsumer<Boolean, Integer> action) {

    }

    @Override
    public void getReviveProgress(Player clientPlayer, BiConsumer<Float, Player> action) {

    }

    @Override
    public void getReviveCount(Player p, Consumer<Integer> action) {

    }

    @Override
    public void getCompleteIncapInfo(Player propPlayer, BiConsumer<Float, Integer> o) {

    }

    @Override
    public int getDeathTicks(Player p) {
        return 0;
    }

    @Override
    public void getCastTime(Player p, float castTime, Consumer<Float> action) {

    }

    @Override
    public void getMaxMana(Player p, Consumer<Float> action) {

    }

    @Override
    public void getClientMana(Player p, BiConsumer<Integer, Integer> action) {

    }

    @Override
    public void getServerMana(ServerPlayer p, BiConsumer<Integer, Integer> action) {

    }

    @Override
    public SpellHolder getSpellInfo(int spellIndex) {
        return null;
    }

    @Override
    public void initParties(MinecraftServer server) {

    }

    @Override
    public void memberAdded(UUID owner, UUID newMember, UUID partyId) {

    }

    @Override
    public void memberLeft(UUID memberLeft) {

    }

    @Override
    public void memberKicked(UUID owner, UUID memberLeft, UUID partyId) {

    }

    @Override
    public void changeLeader(UUID owner, UUID newLeader) {

    }

    @Override
    public void disbandParty(UUID partyId) {

    }

    @Override
    public boolean addPartyMember(UUID initiator, UUID futureMember, boolean finalAttempt) {
        return false;
    }

    @Override
    public boolean removePartyMember(UUID initiator, UUID removedMember, boolean finalAttempt) {
        return false;
    }

    @Override
    public boolean changePartyLeader(UUID newLeader, boolean finalAttempt) {
        return false;
    }

    @Override
    public boolean partyMemberLeft(UUID memberLeaving, boolean finalAttempt) {
        return false;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public String getMainOrigin(Player player) {
        return null;
    }

    @Override
    public void getBleed(Player player, BiConsumer<Boolean, Integer> action) {

    }

    @Override
    public float getReviveProgress(Player clientPlayer) {
        return 0;
    }

    @Override
    public int getThirst(Player player) {
        return 0;
    }

    @Override
    public void setThirstRender(Boolean aBoolean) {

    }

    @Override
    public int getQuench(Player player) {
        return 0;
    }

    @Override
    public void getPlayerTemp(Player player, BiConsumer<Integer, String> action) {

    }

    @Override
    public int getPlayerTemp(Player player) {
        return 0;
    }

    @Override
    public float getThirstTFC(Player player) {
        return 0;
    }

    @Override
    public boolean tempExists() {
        return false;
    }

    @Override
    public void setRenderers(Boolean thirstEnabled, Boolean tempEnabled) {

    }
}
