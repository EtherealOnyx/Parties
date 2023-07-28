package io.sedu.mc.parties.api.mod.origins;

import net.minecraft.world.entity.player.Player;

public interface IOHandler {

    boolean isPresent();

    String getMainOrigin(Player player);

}
