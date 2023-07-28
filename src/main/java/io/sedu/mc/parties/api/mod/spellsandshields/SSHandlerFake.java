package io.sedu.mc.parties.api.mod.spellsandshields;

import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;

public class SSHandlerFake implements ISSHandler {


    @Override
    public void getAllMana(Player player, TriConsumer<Float, Float, Float> action) {

    }

    @Override
    public float getMax(Player player) {
        return 20f;
    }

    @Override
    public void setManaRender(Boolean aBoolean) {

    }


}
