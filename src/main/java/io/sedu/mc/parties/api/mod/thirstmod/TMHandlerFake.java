package io.sedu.mc.parties.api.mod.thirstmod;

import net.minecraft.world.entity.player.Player;

public class TMHandlerFake implements ITMHandler {

    @Override
    public int getThirst(Player player) {
        return 0;
    }

    @Override
    public void setThirstRender(Boolean aBoolean) {

    }

    @Override
    public int getQuench(Player player) {
        return 0;
    }
}
