package io.sedu.mc.parties.api.mod.toughasnails;

import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public interface ITANHandler {

    void getPlayerTemp(Player player, BiConsumer<Integer, String> action);

    int getPlayerTemp(Player player);

    int getThirst(Player player);

    boolean tempExists();

    void setRenderers(Boolean thirstEnabled, Boolean tempEnabled);
}
