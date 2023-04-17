package io.sedu.mc.parties.api.feathers;

import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.server.level.ServerPlayer;

public class FHandlerFake implements IFHandler {


    @Override
    public void getClientFeathers(TriConsumer<Integer, Integer, Integer> action) {

    }

    @Override
    public void getServerFeathers(ServerPlayer player, TriConsumer<Integer, Integer, Integer> action) {

    }

    @Override
    public void setFeathersRender(Boolean aBoolean) {

    }
}
