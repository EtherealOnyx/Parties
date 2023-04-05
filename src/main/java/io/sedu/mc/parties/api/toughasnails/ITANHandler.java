package io.sedu.mc.parties.api.toughasnails;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface ITANHandler {

    void getPlayerTemp(Player player, BiConsumer<Integer, String> action);

    int getPlayerTemp(Player player);

    int getPlayerThirst(Player player);

    boolean tempExists();

    boolean thirstExists();
}
