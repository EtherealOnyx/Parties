package io.sedu.mc.parties.api.mod.ironspellbooks;

import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.spells.CastType;
import io.redspace.ironsspellbooks.spells.SchoolType;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.sedu.mc.parties.client.overlay.anim.CastAnim;
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

    @Override
    public SpellHolder getSpellInfo(int spellIndex) {
        SpellType t = SpellType.getTypeFromValue(spellIndex);
        if (t.getCastType() != CastType.INSTANT) {
            return new SpellHolder(t.getDisplayName(), t.getResourceLocation(), getType(t.getCastType()), getSchool(t.getSchoolType()));
        }
        return CastAnim.EMPTY;
    }

    private SpellHolder.School getSchool(SchoolType schoolType) {
        return switch(schoolType) {
            case FIRE -> SpellHolder.School.FIRE;
            case ICE -> SpellHolder.School.ICE;
            case LIGHTNING -> SpellHolder.School.LIGHTNING;
            case HOLY -> SpellHolder.School.HOLY;
            case ENDER -> SpellHolder.School.ENDER;
            case BLOOD -> SpellHolder.School.BLOOD;
            case EVOCATION -> SpellHolder.School.VOID;
            case POISON -> SpellHolder.School.POISON;
        };
    }

    private SpellHolder.CastType getType(CastType castType) {
        return switch(castType) {
            case NONE, INSTANT, LONG -> SpellHolder.CastType.NORMAL;
            case CONTINUOUS -> SpellHolder.CastType.CHANNEL;
            case CHARGE -> SpellHolder.CastType.HOLD;
        };
    }
}
