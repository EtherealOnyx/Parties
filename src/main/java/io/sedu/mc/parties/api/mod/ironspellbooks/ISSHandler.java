package io.sedu.mc.parties.api.mod.ironspellbooks;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.redspace.ironsspellbooks.registries.AttributeRegistry.MAX_MANA;

public class ISSHandler implements IISSHandler {


    @Override
    public void getCastTime(Player p, float castTime, Consumer<Float> action) {

    }

    @Override
    public void getMaxMana(Player p, Consumer<Float> action) {

    }

    @Override
    public void getClientMana(@Nonnull Player p, BiConsumer<Integer, Integer> action) {
        action.accept(ClientMagicData.getPlayerMana(), (int) p.getAttributeValue(MAX_MANA.get()));
    }

    @Override
    public void getServerMana(@Nonnull ServerPlayer p, BiConsumer<Integer, Integer> action) {
        //Parties.LOGGER.debug("{} has {} mana.", p.getScoreboardName(), PlayerMagicData.getPlayerMagicData(p).getMana());
        action.accept(PlayerMagicData.getPlayerMagicData(p).getMana(), (int) p.getAttributeValue(MAX_MANA.get()));
    }
}
