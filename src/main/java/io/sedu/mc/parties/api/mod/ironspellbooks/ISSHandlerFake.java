package io.sedu.mc.parties.api.mod.ironspellbooks;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ISSHandlerFake implements IISSHandler {

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
}
