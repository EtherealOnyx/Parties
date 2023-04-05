package io.sedu.mc.parties.api.toughasnails;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public class TANHandlerFake implements ITANHandler {


    @Override
    public void getPlayerTemp(Player player, BiConsumer<Integer, String> action) {

    }

    @Override
    public int getPlayerTemp(Player player) {
        return 0;
    }

    @Override
    public int getPlayerThirst(Player player) {
        return 0;
    }

    @Override
    public boolean tempExists() {
        return false;
    }

    @Override
    public boolean thirstExists() {
        return false;
    }

    @Override
    public void setRenderers(Boolean thirstEnabled, Boolean tempEnabled) {

    }
}
