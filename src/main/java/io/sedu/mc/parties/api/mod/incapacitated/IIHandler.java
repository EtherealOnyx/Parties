package io.sedu.mc.parties.api.mod.incapacitated;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IIHandler {

    void getReviveCount(Player p, Consumer<Integer> action);

    void getCompleteIncapInfo(Player propPlayer, BiConsumer<Float, Integer> o);

    int getDeathTicks(Player p);

}
