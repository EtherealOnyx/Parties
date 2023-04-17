package io.sedu.mc.parties.client.overlay;

import io.sedu.mc.parties.util.ColorUtils;
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
        absorbColor = ColorUtils.HSBtoRGB(hue, .3f, 1f);
        bAColorTop = ColorUtils.HSBtoRGB(hue, .5f, 1f);
        bAColorBot = ColorUtils.HSBtoRGB(hue, .4f, .75f);
        colorTopAbsorb = ColorUtils.HSBtoRGB(hue, .75f, 1f);
        colorBotAbsorb = ColorUtils.HSBtoRGB(hue, .9f, .7f);
        colorAbsTop = ColorUtils.HSBtoRGB(hue, .55f, 1f);
        colorAbsBot = ColorUtils.HSBtoRGB(hue, .35f, .69f);
    }

    protected SmallBound setOverflowHue(int d) {
        this.oHue = d;
        setOverflowColors();
        return null;
    }

}
