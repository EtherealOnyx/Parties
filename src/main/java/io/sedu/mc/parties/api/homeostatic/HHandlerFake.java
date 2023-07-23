package io.sedu.mc.parties.api.homeostatic;

import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class HHandlerFake implements IHHandler {


    @Override
    public int getWaterLevel(Player player) {
        return 0;
    }

    @Override
    public void getClientTemperature(Player clientPlayer, TriConsumer<Integer, Integer, Integer> action) {

    }

    @Override
    public void getTemperature(Player player, BiConsumer<Float, Float> action) {

    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public int getBodyTempSev(float data, int severity) {
        return 0;
    }

    @Override
    public int getWorldTempSev(float data, int severity) {
        return 0;
    }

    @Override
    public int convertTemp(float data) {
        return 0;
    }
}
