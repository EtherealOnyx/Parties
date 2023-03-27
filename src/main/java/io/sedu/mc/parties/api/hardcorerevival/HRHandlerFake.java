package io.sedu.mc.parties.api.hardcorerevival;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class HRHandlerFake implements IHRHandler {

    @Override
    public void getDowned(Player player, BiConsumer<Boolean, Integer> action) {

    }

    @Override
    public void getReviveProgress(Player clientPlayer, BiConsumer<Float, Player> action) {
    }
}
