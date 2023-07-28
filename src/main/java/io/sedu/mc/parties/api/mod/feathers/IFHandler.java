package io.sedu.mc.parties.api.mod.feathers;

import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.server.level.ServerPlayer;

public interface IFHandler {

    void getClientFeathers(TriConsumer<Integer, Integer, Integer> action);

    void getServerFeathers(ServerPlayer player, TriConsumer<Integer, Integer, Integer> action);

    void setFeathersRender(Boolean aBoolean);
}
