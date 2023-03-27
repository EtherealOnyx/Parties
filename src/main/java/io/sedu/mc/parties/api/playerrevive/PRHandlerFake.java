package io.sedu.mc.parties.api.playerrevive;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class PRHandlerFake implements IPRHandler {


    @Override
    public void getBleed(Player player, BiConsumer<Boolean, Integer> action) {
    }

    @Override
    public float getReviveProgress(Player clientPlayer) {
        return 0;
    }
}
