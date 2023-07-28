package io.sedu.mc.parties.api.mod.dietarystats;

import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface IDSHandler {


    void getMaxHunger(Player p, Consumer<Float> action);
}
