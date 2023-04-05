package io.sedu.mc.parties.api.arsnoveau;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface IANHandler {


    float getCurrentMana(Player player);

    int getMaxMana(Player player);

    boolean exists();

    void getManaValues(Player player, BiConsumer<Float, Integer> action);
}
