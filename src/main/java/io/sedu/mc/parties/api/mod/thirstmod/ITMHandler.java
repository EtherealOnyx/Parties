package io.sedu.mc.parties.api.mod.thirstmod;

import net.minecraft.world.entity.player.Player;

public interface ITMHandler {

    int getThirst(Player player);

    void setThirstRender(Boolean aBoolean);

    int getQuench(Player player);
}
