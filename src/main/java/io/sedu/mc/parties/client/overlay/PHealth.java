package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.HealthAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;
import static io.sedu.mc.parties.util.AnimUtils.animPos;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PHealth extends RenderIconTextItem {

    int absorbColor;
    int deadColor;

    int colorTop;
    int colorBot;
    int colorTopMissing;
    int colorBotMissing;
    int colorTopAbsorb;
    int colorBotAbsorb;
    
    int colorIncTop;
    int colorIncBot;
    int colorAbsTop;
    int colorAbsBot;
    int colorDecTop;
    int colorDecBot;

    int bColorTop;
    int bColorBot;
    int bAColorTop;
    int bAColorBot;

    public PHealth(String name) {
        super(name);
    }

    @Override
    int getColor() {
        return 0xe3403d;
    }

    @Override
    public String getType() {
        return "Bar";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderUtils.sizeRect(poseStack.last().pose(), b.x+7, b.y+9, 0, 22, 7, bColorTop, bColorBot);
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+10, 0, 20, 5, colorTop, colorBot);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack,b.x+3, b.y+8, 16, 0, 9, 9);
        blit(poseStack,b.x+3, b.y+8, 52, 0, 9, 9);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {

        if (id.isDead) {
            RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing);

            textCentered(i, tX(i), tY(i), gui, poseStack, "Dead", deadColor);
            return;
        }
        if (iconEnabled) {
            renderHealth(i, poseStack, id);
            if (id.health.active)
                renderHealthAnim(i, poseStack, id, partialTicks);



            //Dimmer
            RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 255 - id.alphaI << 24);
            if (notEditing() && withinBounds(x(i), y(i), width, height, 2, scale)) {
                renderTooltip(poseStack, gui, 10, 0, "Health: " + (id.health.cur + id.health.absorb) + "/" + id.health.max, 0xfc807c, 0x4d110f, 0xffbfbd);
            }
        }
        if (textEnabled)
            if (id.health.absorb > 0) {
                textCentered(i, tX(i), tY(i), gui, poseStack, id.health.healthText, absorbColor);
            } else {
                textCentered(i, tX(i), tY(i), gui, poseStack, id.health.healthText, color);
            }





    }

    private void renderHealth(int i, PoseStack poseStack, ClientPlayerData id) {

        float hB, aB;
        hB = id.health.getPercent();
        if (id.health.absorb > 0) {
            RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, bAColorTop, bAColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            aB = hB + id.health.getPercentA();
            rectRNoA(poseStack, i, zPos, hB, colorTop, colorBot); //Health
            rectB(poseStack, i, zPos, hB, aB, colorTopAbsorb, colorBotAbsorb); //Absorb
        } else {
            RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorTop, bColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            rectRNoA(poseStack, i, zPos, hB, colorTop, colorBot); //Health
        }
    }

    private void rectRNoA(PoseStack p, int i, int zLevel, float rightPosition, int startColor, int endColor ) {
        RenderUtils.sizeRectNoA(p.last().pose(), x(i)+1, y(i)+1, zLevel, Math.min(width - 1, ((width-2)*rightPosition)), height-2, startColor, endColor);
    }

    private void rectB(PoseStack p, int i, int zLevel, float leftPosition, float rightPosition, int startColor, int endColor ) {
        RenderUtils.rect(p.last().pose(), zPos, Math.max(x(i), x(i) + (width-2)*leftPosition)+1, y(i)+1, Math.min(x(i)+width-1, x(i)+1 + (width-2)*rightPosition), y(i)+height-1, startColor, endColor);
        //Render.rect(p.last().pose(), zLevel, l(i)+width*leftPosition-1, t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }

    private void rectAnim(PoseStack p, int i, int zLevel, float leftPosition, float rightPosition, int startColor, int endColor ) {
        //TODO: Use a left top right bot rect. Width stays untouched. Left pos must be + 1.
        //TODO: width-2 * (rightpos-leftpos) ???
        //TODO: X Bound: x + 1 + (width-2)*leftPos??? IS THIS ALL I NEEDED OMG
        //TODO: Switch to left, top, right, bottom and use Math.max(x+1, val) etc to clamp values.
        //Left Pos: x + offset + (width - offset*2)*leftPos | Right Pos: x + offset + (
        RenderUtils.rectNoA(p.last().pose(), zPos, Math.max(x(i), x(i) + (width-2)*leftPosition)+1, y(i)+1, Math.min(x(i)+width-1, x(i)+1 + (width-2)*rightPosition), y(i)+height-1, startColor, endColor);
        //Render.rectNoA(p.last().pose(), zLevel, l(i)+width*leftPosition-1, t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }

    private void renderHealthAnim(int i, PoseStack poseStack, ClientPlayerData id, float partialTicks) {
        if (id.health.animTime - partialTicks < 10) {
            id.health.oldH += (id.health.curH - id.health.oldH) * animPos(10 - id.health.animTime, partialTicks, true, 10, 1);
            id.health.oldA += (id.health.curA - id.health.oldA) * animPos(10 - id.health.animTime, partialTicks, true, 10, 1);
        }

        if (id.health.hInc) {
            if (id.health.effHOld())
                rectAnim(poseStack, i, zPos, id.health.oldH, id.health.curH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, zPos, id.health.oldH, id.health.curH, colorIncTop, colorIncBot);

        } else {
            if (id.health.effH())
                rectAnim(poseStack, i, zPos, id.health.curH, id.health.oldH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, zPos, id.health.curH, id.health.oldH, colorDecTop, colorDecBot);

        }

        if (id.health.aInc)
            rectAnim(poseStack, i, zPos, id.health.oldA, id.health.curA, colorAbsTop, colorAbsBot);
        else
            rectAnim(poseStack, i, zPos, id.health.curA, id.health.oldA, colorAbsTop, colorAbsBot);

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
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("general");
        c.addBooleanEntry("display", isEnabled());
        c.addSliderEntry("scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addTitleEntry("icon");
        c.addBooleanEntry("idisplay", iconEnabled);
        c.addSliderEntry("xpos", 0, this::maxX, this.x, true);
        c.addSliderEntry("ypos", 0, this::maxY, this.y, true);
        c.addSliderEntry("width", 1, this::maxW, width, true);
        c.addSliderEntry("height", 1, this::maxH, height, true);
        c.addTitleEntry("text");
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("tshadow", textShadow);
        c.addSliderEntry("ttype", 0, () -> 2, HealthAnim.type);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> frameEleW, textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> frameEleH, textY));
        toggleTextAttach(entries);
        c.addTitleEntry("textc");
        c.addColorEntry("tcolor", color);
        c.addColorEntry("tcabsorb", absorbColor);
        c.addColorEntry("tcdead", deadColor);
        c.addSpaceEntry();
        c.addTitleEntry("barc");
        c.addSpaceEntry();
        c.addTitleEntry("barb");
        c.addColorEntry("bbct", bColorTop);
        c.addColorEntry("bbcb", bColorBot);
        c.addSpaceEntry();
        c.addTitleEntry("bara");
        c.addColorEntry("bbact", bAColorTop);
        c.addColorEntry("bbacb", bAColorBot);
        c.addSpaceEntry();
        c.addTitleEntry("bc");
        c.addColorEntry("bct", colorTop);
        c.addColorEntry("bcb", colorBot);
        c.addSpaceEntry();
        c.addTitleEntry("bcm");
        c.addColorEntry("bctm", colorTopMissing);
        c.addColorEntry("bcbm", colorBotMissing);
        c.addSpaceEntry();
        c.addTitleEntry("bca");
        c.addColorEntry("bcta", colorTopAbsorb);
        c.addColorEntry("bcba", colorBotAbsorb);
        c.addSpaceEntry();
        c.addTitleEntry("baa");
        c.addColorEntry("bcat", colorAbsTop);
        c.addColorEntry("bcab", colorAbsBot);
        c.addSpaceEntry();
        c.addTitleEntry("bai");
        c.addColorEntry("bcit", colorIncTop);
        c.addColorEntry("bcib", colorIncBot);
        c.addSpaceEntry();
        c.addTitleEntry("bad");
        c.addColorEntry("bcdt", colorDecTop);
        c.addColorEntry("bcdb", colorDecBot);
        c.addSpaceEntry();

        return c;
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



    @Override
    public void setColor(int type, int data) {
        switch(type) {
            case 0 -> color = data;
            case 1 -> absorbColor = data;
            case 2 -> deadColor = data;
            case 3 -> bColorTop = data | 0xCC << 24;
            case 4 -> bColorBot = data | 0xCC << 24;
            case 5 -> bAColorTop = data | 0xCC << 24;
            case 6 -> bAColorBot = data | 0xCC << 24;
            case 7 -> colorTop = data;
            case 8 -> colorBot = data;
            case 9 -> colorTopMissing = data;
            case 10 -> colorBotMissing = data;
            case 11 -> colorTopAbsorb = data | 0xCC << 24;
            case 12 -> colorBotAbsorb = data | 0xCC << 24;
            case 13 -> colorAbsTop = data;
            case 14 -> colorAbsBot = data;
            case 15 -> colorIncTop = data;
            case 16 -> colorIncBot = data;
            case 17 -> colorDecTop = data;
            case 18 -> colorDecBot = data;
        }
    }

    @Override
    public int getColor(int type) {
        switch(type) {
            case 0 -> {return color;}
            case 1 -> {return absorbColor;}
            case 2 -> {return deadColor;}
            case 3 -> {return bColorTop;}
            case 4 -> {return bColorBot;}
            case 5 -> {return bAColorTop;}
            case 6 -> {return bAColorBot;}
            case 7 -> {return colorTop;}
            case 8 -> {return colorBot;}
            case 9 -> {return colorTopMissing;}
            case 10 -> {return colorBotMissing;}
            case 11 -> {return colorTopAbsorb;}
            case 12 -> {return colorBotAbsorb;}
            case 13 -> {return colorAbsTop;}
            case 14 -> {return colorAbsBot;}
            case 15 -> {return colorIncTop;}
            case 16 -> {return colorIncBot;}
            case 17 -> {return colorDecTop;}
            case 18 -> {return colorDecBot;}
        }
        return 0;
    }

    @Override
    ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true);
        e.addEntry("scale", 2);
        e.addEntry("zpos", 0);
        e.addEntry("idisplay", true);
        e.addEntry("xpos", 46);
        e.addEntry("ypos", 29);
        e.addEntry("width", 120);
        e.addEntry("height", 10);
        e.addEntry("tdisplay", true);
        e.addEntry("tshadow", true);
        e.addEntry("ttype", 0);
        e.addEntry("tattached", true);
        e.addEntry("xtpos", 0);
        e.addEntry("ytpos", 0);
        e.addEntry("tcolor", 0xffe3e3);
        e.addEntry("tcabsorb", 0xfff399);
        e.addEntry("tcdead", 0x530404);
        e.addEntry("bbct", 0xCC111111);
        e.addEntry("bbcb", 0xCC555555);
        e.addEntry("bbact", 0xCCfaf098);
        e.addEntry("bbacb", 0xCCd9cd68);
        e.addEntry("bct", 0xC52C27);
        e.addEntry("bcb", 0x6C0d15);
        e.addEntry("bctm", 0x450202);
        e.addEntry("bcbm", 0x620909);
        e.addEntry("bcta", 0xCCFFCD42);
        e.addEntry("bcba", 0xCCB08610);
        e.addEntry("bcat", 0xFFCD72);
        e.addEntry("bcab", 0xB08672);
        e.addEntry("bcit", 0xC5FFC5);
        e.addEntry("bcib", 0x6CFF6C);
        e.addEntry("bcdt", 0xFFC5C5);
        e.addEntry("bcdb", 0xFF6C6C);
        return e;
    }


}
