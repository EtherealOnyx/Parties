package io.sedu.mc.parties.api.mod.coldsweat;

import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface ICSHandler {
    float getWorldTemp(Player player);

    float getBodyTemp(Player player);

    void convertTemp(float worldTemp, BiConsumer<Integer, Integer> action);

    void getClientWorldTemp(Player clientPlayer, TriConsumer<Integer, Integer, Integer> action);

    boolean exists();

    void setTempRender(Boolean renderTemp);
}
