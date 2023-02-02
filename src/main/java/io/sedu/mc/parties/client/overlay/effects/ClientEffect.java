package io.sedu.mc.parties.client.overlay.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class ClientEffect {
    private final MobEffect type;

    private String display;
    private String roman;
    private int offset;
    boolean removal = false;
    Effect cur;

    public void addEffect(int duration, int amp) {
        if (isInstant()) {
            cur.amp = amp;
            cur.duration = 1;
            roman = roman(cur.amp);
            return;
        }

        cur.addEffect(new Effect(duration, amp));
        calculate();
        roman = roman(cur.amp);
        removal = false;
    }

    public boolean isInstant() {
        return type == MobEffects.HEAL
                || type == MobEffects.HARM;
    }

    public boolean bene() {
        return type.isBeneficial();
    }

    public String getRoman() {
        return roman;
    }

    public String getAmp() {
        return String.valueOf(cur.amp+1);
    }

    public boolean isDying() {
        return cur.duration < 10;
    }

    class Effect { //TODO: Tick all hidden effects. Remove them if the duration < 0. Remove entire effect structure on expirey event from server (markForRemoval), Implement amp tracker.
        int duration; //In seconds
        int amp;
        Effect hidden;

        public Effect(int duration, int amp) {
            this.duration = duration;
            this.amp = amp;
        }

        public Effect(Effect e) {
            duration = e.duration;
            amp = e.amp;
            hidden = e.hidden;
        }

        private void internalTick() {
            if (hidden != null)
                this.hidden.internalTick();

            this.duration--;
        }

        private void addEffect(Effect other) {
            if (other.amp > amp) {//If New Effect > Old Effect
                if (other.duration < duration) { //If new effect duration < old effect duration
                    //Effect oldestEffect = hidden;
                    hidden = new Effect(this);
                    //hidden.hidden = oldestEffect;
                }
                amp = other.amp;
                duration = other.duration;
            } else if (other.duration > duration) {
                if (other.amp == amp) {
                    duration = other.duration;
                } else if (cur.hidden == null) {
                    hidden = new Effect(other);
                } else {
                    hidden.addEffect(other);
                }
            }
        }
    }

    private int tick() {
        cur.internalTick();
        if (cur.duration == 0 && cur.hidden != null) {
            cur.duration = cur.hidden.duration;
            cur.amp = cur.hidden.amp;
            cur.hidden = cur.hidden.hidden;
            roman = roman(cur.amp);
        }
        return cur.duration;
    }

    public ClientEffect(int type, int duration, int amp) {
        this.type = MobEffect.byId(type);
        cur = new Effect(duration, amp);
        roman = roman(cur.amp);
        if (isInstant()) {
            cur.duration = 1;
            return;
        }
        this.cur.duration = duration;
        calculate();
    }

    private static String roman(int num) {
        if (num == 0)
            return "";
        num++;
        if (num > 9)
            return "★";
        int[] values = {9,5,4,1};
        String[] romanLetters = {"IX","V","IV","I"};
        StringBuilder roman = new StringBuilder();
        for(int i=0;i<values.length;i++)
        {
            while(num >= values[i])
            {
                num = num - values[i];
                roman.append(romanLetters[i]);
            }
        }
        return roman.toString();
    }


    private void calculate() {
        if (isInstant())
            return;
        if (cur.duration < 60)  updateVal(1, 's');
        else if (cur.duration < 3600) updateVal(60, 'm');
        else if (cur.duration < 86400) updateVal(3600, 'h');
        else if (cur.duration < 604800) updateVal(86400, 'd');
        else if (cur.duration < 31536000)  updateVal(604800, 'w');
        else updateVal(31536000, 'y');

    }

    public MobEffect getEffect() {
        return type;
    }

    public int getId() {
        return MobEffect.getId(type);
    }

    private void updateVal(int divisor, char type) {
        display = String.valueOf(cur.duration / divisor) + type;
        offset = display.length() == 3 ? -3 : 0;
    }

    public String getDisplay() {
        return display;
    }

    public int getOffset() {
        return offset;
    }

    public boolean update() {
        if (tick() >= 0)
            calculate();
        else {
            display = "";
            if (isInstant()) removal = true;
        }

        return removal;
    }

    public void markForRemoval() {
        if (!isInstant())
            removal = true;
    }
}
