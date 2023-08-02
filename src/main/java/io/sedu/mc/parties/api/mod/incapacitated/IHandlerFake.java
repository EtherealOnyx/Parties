package io.sedu.mc.parties.api.mod.incapacitated;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IHandlerFake implements IIHandler {

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
}
