package io.sedu.mc.parties.client.overlay.anim;

import io.sedu.mc.parties.api.mod.ironspellbooks.ISSCompatManager;
import io.sedu.mc.parties.api.mod.ironspellbooks.SpellHolder;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

import static io.sedu.mc.parties.api.mod.ironspellbooks.SpellHolder.CastType.CHANNEL;
import static io.sedu.mc.parties.api.mod.ironspellbooks.SpellHolder.CastType.HOLD;

public class CastAnim extends AnimHandlerBase {

    public static final SpellHolder EMPTY = new SpellHolder(new TextComponent(""), new ResourceLocation(""), SpellHolder.CastType.NORMAL, SpellHolder.School.NONE);
    SpellHolder spell = EMPTY;
    private boolean finished = false;

    public CastAnim(int length) {
        super(length);
        //TODO: Get duration converted to ticks.
    }

    @Override
    void activateValues(Object... data) {
        setupSpell((Integer)data[0]);
        length = spell == EMPTY ? 0 : (Integer)data[1];
    }

    private void setupSpell(int type) {
        spell = ISSCompatManager.getHandler().getSpellInfo(type);
        finished = false;
        //if (spell == null) return;
    }

    public void getSpell(float partialTicks, BiConsumer<SpellHolder, Float> action) {
        action.accept(spell, getCompletionPercent(partialTicks));
    }

    private float getCompletionPercent(float partialTicks) {
        return Math.min(1f, (spell.type == CHANNEL ? animTime - partialTicks : length - animTime + partialTicks) / length);
    }

    public void markDone() {
        finished = true;
    }

    @Override
    boolean tickAnim() {
        if (finished) {
            active = false;
            animTime = 0;
            return true;
        }
        animTime -= 1;
        if (animTime <= 0) {
            animTime = 0;
            if (spell.type != HOLD) {
                active = false;
                return true;
            }
        }
        return false;
    }
}
