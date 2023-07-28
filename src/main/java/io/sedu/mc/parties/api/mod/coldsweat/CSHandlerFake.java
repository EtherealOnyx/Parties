package io.sedu.mc.parties.api.mod.coldsweat;

import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class CSHandlerFake implements ICSHandler {

    @Override
    public float getWorldTemp(Player player) {
        return 0;
    }

    @Override
    public float getBodyTemp(Player player) {
        return 0;
    }

    @Override
    public void convertTemp(float worldTemp, BiConsumer<Integer, Integer> action) {

    }

    @Override
    public void getClientWorldTemp(Player player, TriConsumer<Integer, Integer, Integer> triConsumer) {

    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public void setTempRender(Boolean renderTemp) {

    }

}
