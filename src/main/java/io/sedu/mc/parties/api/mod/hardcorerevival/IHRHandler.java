package io.sedu.mc.parties.api.mod.hardcorerevival;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface IHRHandler {

    void getDowned(Player player, BiConsumer<Boolean, Integer> action);
    void getReviveProgress(Player clientPlayer, BiConsumer<Float, Player> action);
}
