package io.sedu.mc.parties.api.thirstmod;

import net.minecraft.world.entity.player.Player;

public class TMHandlerFake implements ITMHandler {

    @Override
    public int getThirst(Player player) {
        return 0;
    }
}
