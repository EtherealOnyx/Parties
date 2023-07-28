package io.sedu.mc.parties.api.mod.ironspellbooks;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IISSHandler {

    void getCastTime(Player p, float castTime, Consumer<Float> action);

    void getMaxMana(Player p, Consumer<Float> action);

    void getClientMana(Player p, BiConsumer<Integer, Integer> action);

    void getServerMana(ServerPlayer p, BiConsumer<Integer, Integer> action);


    SpellHolder getSpellInfo(int spellIndex);
}
