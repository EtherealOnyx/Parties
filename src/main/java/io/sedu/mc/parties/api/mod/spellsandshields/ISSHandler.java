package io.sedu.mc.parties.api.mod.spellsandshields;

import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;

public interface ISSHandler {

    void getAllMana(Player player, TriConsumer<Float, Float, Float> action);

    float getMax(Player player);

    void setManaRender(Boolean aBoolean);
}
