package io.sedu.mc.parties.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;

public class ClientEffect {
    private int duration; //In seconds
    private String display;
    private int offset;
    private char suffix;

    private MobEffect type;

    ClientEffect(int type, int duration) {
        this.type = MobEffect.byId(type);
        this.duration = duration;
        calculate();
    }

    public MobEffect getEffect() {
        return type;
    }

    private void calculate() {
        int divisor = 1;
        char type = 's';
        if (duration > 31536000) //Year
        {
            divisor = 31536000;
            type = 'y';
        } else if (duration > 604800) //Week
        {
            divisor = 86400;
            type = 'w';
        } else if (duration > 86400) //Day
        {
            divisor = 86400;
            type = 'd';
        } else if (duration > 3600) // Hour
        {
            divisor = 3600;
            type = 'h';
        } else if (duration > 60) // Minute
        {
            divisor = 60;
            type = 'm';
        }
        if (suffix == type)
            return;
        display = String.valueOf(duration/divisor) + type;
        if (display.length() == 3)
            offset -= 3;
        suffix = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public int getOffset() {
        return offset;
    }

}
