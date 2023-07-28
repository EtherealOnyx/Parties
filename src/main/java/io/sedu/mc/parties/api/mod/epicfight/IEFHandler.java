package io.sedu.mc.parties.api.mod.epicfight;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface IEFHandler {

    void getClientValues(Player player, BiConsumer<Float, Integer> action);
}
