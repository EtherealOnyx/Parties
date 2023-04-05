package io.sedu.mc.parties.api.coldsweat;

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
    public void getClientWorldTemp(Player clientPlayer, TriConsumer<Integer, Integer, Integer> action) {

    }

    @Override
    public boolean exists() {
        return false;
    }

}
