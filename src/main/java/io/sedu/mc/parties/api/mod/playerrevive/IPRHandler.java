package io.sedu.mc.parties.api.mod.playerrevive;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface IPRHandler {

    void getBleed(Player player, BiConsumer<Boolean, Integer> action);

    float getReviveProgress(Player clientPlayer);
}
