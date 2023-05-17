package io.sedu.mc.parties.client.overlay;

import io.sedu.mc.parties.api.helper.ColorAPI;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class OverflowBarBase extends BarBase {
    int oHue = 0;
    int absorbColor;
    int bAColorTop;
    int bAColorBot;
    int colorTopAbsorb;
    int colorBotAbsorb;
    int colorAbsTop;
    int colorAbsBot;

    public OverflowBarBase(String name, TranslatableComponent c) {
        super(name, c);
    }

    protected void setOverflowColors() {
        float hue = this.oHue/100f;
        absorbColor = ColorAPI.HSBtoRGB(hue, .3f, 1f);
        bAColorTop = ColorAPI.HSBtoRGB(hue, .5f, 1f);
        bAColorBot = ColorAPI.HSBtoRGB(hue, .4f, .75f);
        colorTopAbsorb = ColorAPI.HSBtoRGB(hue, .75f, 1f);
        colorBotAbsorb = ColorAPI.HSBtoRGB(hue, .9f, .7f);
        colorAbsTop = ColorAPI.HSBtoRGB(hue, .55f, 1f);
        colorAbsBot = ColorAPI.HSBtoRGB(hue, .35f, .69f);
    }

    protected SmallBound setOverflowHue(int d) {
        this.oHue = d;
        setOverflowColors();
        return null;
    }

}
