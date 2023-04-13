package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.ColorUtils;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

public abstract class BarBase extends RenderIconTextItem implements TooltipItem {
    protected final TranslatableComponent tipName;

    int hue = 0;

    int deadColor;

    int colorTop;
    int colorBot;
    int colorTopMissing;
    int colorBotMissing;

    int colorIncTop;
    int colorIncBot;
    int colorDecTop;
    int colorDecBot;

    int bColorTop;
    int bColorBot;
    private static BarBase.Renderer renderLastDimmer;

    public BarBase(String name, TranslatableComponent c) {
        super(name);
        this.tipName = c;
        renderLastDimmer = (i, id, poseStack, barBase) -> {

        };
    }

    @Override
    int getColor() {
        return colorTop;
    }

    @Override
    public String getType() {
        return "Bar";
    }

    @Override
    protected abstract void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b);

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isSpectator) return;
        renderSelfBar(i, id, gui, poseStack, partialTicks);
        renderLastDimmer.render(i, id, poseStack, this);
    }

    protected abstract void renderSelfBar(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                          float partialTicks);

    public static void updateRendererForMods() {
        renderLastDimmer = ((i, id, poseStack, barItem) -> {
            if (id.bleedOrDowned()) {
                RenderUtils.sizeRect(poseStack.last().pose(), barItem.x(i), barItem.y(i), barItem.zPos, barItem.width, barItem.height, 150 << 24);
            }
        });
    }

    protected void rectRNoA(PoseStack p, int i, float rightPosition, int startColor, int endColor ) {
        RenderUtils.sizeRectNoA(p.last().pose(), x(i)+1, y(i)+1, zPos, Math.min(width - 1, ((width-2)*rightPosition)), height-2, startColor, endColor);
    }

    protected void rectB(PoseStack p, int i, float leftPosition, float rightPosition, int startColor, int endColor ) {
        RenderUtils.rectNoA(p.last().pose(), zPos, Math.max(x(i), x(i) + (width-2)*leftPosition)+1, y(i)+1, Math.min(x(i)+width-1, x(i)+1 + (width-2)*rightPosition), y(i)+height-1, startColor, endColor);
        //Render.rect(p.last().pose(), zLevel, l(i)+width*leftPosition-1, t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }

    protected void rectAnim(PoseStack p, int i, float leftPosition, float rightPosition, int startColor, int endColor ) {
        //Left Pos: x + offset + (width - offset*2)*leftPos | Right Pos: x + offset + (
        RenderUtils.rectNoA(p.last().pose(), zPos, Math.max(x(i), x(i) + (width-2)*leftPosition)+1, y(i)+1, Math.min(x(i)+width-1, x(i)+1 + (width-2)*rightPosition), y(i)+height-1, startColor, endColor);
        //Render.rectNoA(p.last().pose(), zLevel, l(i)+width*leftPosition-1, t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }


    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + (width>>1);
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + (height>>1) + 1;
    }


    @Override
    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
        width = Mth.clamp(width, 0, maxW());
        height = Mth.clamp(height, 0, maxH());
    }

    protected int maxW() {
        return (int) Math.ceil(frameEleW/scale);
    }

    protected int maxH() {
        return (int) Math.ceil(frameEleH/scale);
    }

    protected void setMainColors() {
        float hue = this.hue/100f;
        bColorTop = ColorUtils.HSBtoRGB(hue, .5f, .25f);
        bColorBot = ColorUtils.HSBtoRGB(hue, .25f, .5f);
        color = ColorUtils.HSBtoRGB(hue, .2f, 1f);
        deadColor = ColorUtils.HSBtoRGB(hue, .25f, .75f);
        colorTop = ColorUtils.HSBtoRGB(hue, .8f, .77f);
        colorBot = ColorUtils.HSBtoRGB(hue, .88f, .42f);
        colorTopMissing = ColorUtils.HSBtoRGB(hue, .97f, .27f);
        colorBotMissing = ColorUtils.HSBtoRGB(hue, .91f, .38f);
    }

    @Override
    public RenderItem.SmallBound setColor(int type, int data) {
        switch(type) {
            case 0 -> colorIncTop = data;
            case 1 -> colorIncBot = data;
            case 2 -> colorDecTop = data;
            case 3 -> colorDecBot = data;
        }
        return null;
    }

    @Override
    public int getColor(int type) {
        switch(type) {
            case 0 -> {return colorIncTop;}
            case 1 -> {return colorIncBot;}
            case 2 -> {return colorDecTop;}
            case 3 -> {return colorDecBot;}
        }
        return 0;
    }

    protected RenderItem.SmallBound setMainHue(int d) {
        this.hue = d;
        setMainColors();
        return null;
    }

    @Override
    public abstract void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY);

    @Override
    public abstract ConfigEntry getDefaults();

    @Override
    protected abstract ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse);

    public abstract SmallBound setTextType(int d);

    public abstract int getTextType();

    private interface Renderer {
        void render(int i, ClientPlayerData id, PoseStack poseStack, BarBase barItem);
    }
}
