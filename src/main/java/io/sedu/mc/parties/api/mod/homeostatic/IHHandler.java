package io.sedu.mc.parties.api.mod.homeostatic;

import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface IHHandler {


    int getWaterLevel(Player player);

    void getClientTemperature(Player clientPlayer, TriConsumer<Integer, Integer, Integer> action);

    void getTemperature(Player player, BiConsumer<Float, Float> action);

    boolean exists();

    int getBodyTempSev(float data, int severity);

    int getWorldTempSev(float data, int severity);

    int convertTemp(float data);
}
