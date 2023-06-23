package io.sedu.mc.parties.api.mod.origins;

import net.minecraft.world.entity.player.Player;

public class OHandlerFake implements IOHandler {


    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public String getMainOrigin(Player player) {
        return "";
    }

}
