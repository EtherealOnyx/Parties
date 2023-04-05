package io.sedu.mc.parties.api.arsnoveau;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class ANHandlerFake implements IANHandler {


    @Override
    public float getCurrentMana(Player player) {
        return 0;
    }

    @Override
    public int getMaxMana(Player player) {
        return 0;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public void getManaValues(Player player, BiConsumer<Float, Integer> action) {

    }

    @Override
    public void setManaRender(Boolean renderMana) {

    }
}
