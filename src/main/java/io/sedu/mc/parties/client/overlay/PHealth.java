package io.sedu.mc.parties.client.overlay;

import Util.Render;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.anim.HealthAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;
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
    int healthType;

    public PHealth(String name, int x, int y, int width, int height, int color, int absorbColor, int deadColor) {
        super(name, x, y, width, height, color, true);
        this.absorbColor = absorbColor;
        this.deadColor = deadColor;
        colorTop = 0xC52C27;
        colorBot = 0x6C0D15;
        colorTopMissing = 0x450202;
        colorBotMissing = 0x620909;
        colorTopAbsorb = 0xFFCD42 | 0xCC << 24;
        colorBotAbsorb = 0xB08610 | 0xCC << 24;
        colorAbsTop = 0xFFCD72;
        colorAbsBot = 0xB08672;
        colorIncTop = 0xC5FFC5;
        colorIncBot = 0x6CFF6C;
        colorDecTop = 0xFFC5C5;
        colorDecBot = 0xFF6C6C;
        bAColorTop = 0xfaf098 | 0xCC << 24;
        bAColorBot = 0xd9cd68 | 0xCC << 24;
        bColorTop = 0x111111 | 0xCC << 24;
        bColorBot = 0x555555 | 0xCC << 24;
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
        Render.sizeRect(poseStack.last().pose(), b.x+7, b.y+9, 0, 22, 7, bColorTop, bColorBot);
        Render.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+10, 0, 20, 5, colorTop, colorBot);
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
            Render.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorBot);
            Render.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing);

            textCentered(i, tX(i), tY(i), gui, poseStack, "Dead", deadColor);
            return;
        }
        if (iconEnabled) {
            renderHealth(i, poseStack, id);
            if (id.health.active)
                renderHealthAnim(i, poseStack, id, partialTicks);



            //Dimmer
            Render.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 255 - id.alphaI << 24);
            if (notEditing() && withinBounds(l(i), t(i), r(i), b(i), 2)) {
                renderTooltip(poseStack, gui, 10, 0, "Health: " + (id.health.cur + id.health.absorb) + "/" + id.health.max, 0xfc807c, 0x4d110f, 0xffbfbd);
            }
        }
        if (textEnabled)
            if (id.health.absorb > 0) {
                textCentered(i, tX(i), tY(i), gui, poseStack, (int)Math.ceil(id.health.cur+id.health.absorb) + "/" + (int)id.health.max, absorbColor);
            } else {
                textCentered(i, tX(i), tY(i), gui, poseStack, (int)Math.ceil(id.health.cur) + "/" + (int)id.health.max, color);
            }





    }

    private void renderHealth(int i, PoseStack poseStack, ClientPlayerData id) {

        float hB, aB;
        hB = HealthAnim.getPercent(id.health.cur, id.health.max, id.health.absorb);
        if (id.health.absorb > 0) {
            Render.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, bAColorTop, bAColorBot);
            Render.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            aB = hB + HealthAnim.getPercentA(id.health.cur, id.health.max, id.health.absorb);
            rectRNoA(poseStack, i, zPos, hB, colorTop, colorBot); //Health
            rectB(poseStack, i, zPos, hB, aB, colorTopAbsorb, colorBotAbsorb); //Absorb
        } else {
            Render.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorTop, bColorBot);
            Render.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            rectRNoA(poseStack, i, zPos, hB, colorTop, colorBot); //Health
        }
    }

    private void rectRNoA(PoseStack p, int i, int zLevel, float rightPosition, int startColor, int endColor ) {
        Render.sizeRectNoA(p.last().pose(), x(i)+1, y(i)+1, zLevel, Math.min(width - 2, ((width-2)*rightPosition)), height-2, startColor, endColor);
    }

    private void rectB(PoseStack p, int i, int zLevel, float leftPosition, float rightPosition, int startColor, int endColor ) {
        Render.sizeRect(p.last().pose(), x(i) + width*leftPosition, y(i) + 1, zLevel, Mth.clamp(width*(rightPosition-leftPosition), 0,width-2), height-2, startColor, endColor);
        //Render.rect(p.last().pose(), zLevel, l(i)+width*leftPosition-1, t(i)+1, l(i)-1+width*rightPosition, b(i)-1, startColor, endColor);
    }

    private void rectAnim(PoseStack p, int i, int zLevel, float leftPosition, float rightPosition, int startColor, int endColor ) {
        //TODO: Use a left top right bot rect. Width stays untouched. Left pos must be + 1.
        //TODO: width-2 * (rightpos-leftpos) ???
        //TODO: X Bound: x + 1 + (width-2)*leftPos??? IS THIS ALL I NEEDED OMG
        //TODO: Switch to left, top, right, bottom and use Math.max(x+1, val) etc to clamp values.
        //Left Pos: x + offset + (width - offset*2)*leftPos | Right Pos: x + offset + (
        Render.rectNoA(p.last().pose(), zPos, Math.max(x(i),x(i) + (width-2)*leftPosition)+1, y(i)+1,Math.min(x(i)+width-1,x(i)+1 + (width-2)*rightPosition), y(i)+height-1, startColor, endColor);
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
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h);
        c.addTitleEntry("config.sedparties.title.general");
        c.addBooleanEntry("config.sedparties.name.display", isEnabled());
        c.addSliderEntry("config.sedparties.name.scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("config.sedparties.name.zpos", 0, () -> 10, zPos);

        c.addTitleEntry("config.sedparties.title.icon");
        c.addBooleanEntry("config.sedparties.name.idisplay", iconEnabled);
        c.addSliderEntry("config.sedparties.name.xpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX - (int)(width*scale)), this.x, true);
        c.addSliderEntry("config.sedparties.name.ypos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int)(height*scale)), this.y, true);
        c.addSliderEntry("config.sedparties.name.width", 1, () -> (int) Math.ceil((Math.max(clickArea.x + clickArea.w(), frameW) - x)/scale), width, true);
        c.addSliderEntry("config.sedparties.name.height", 1, () -> (int) Math.ceil((Math.max(clickArea.y + clickArea.h(), frameH) - y)/scale), height, true);

        c.addTitleEntry("config.sedparties.title.text");
        c.addBooleanEntry("config.sedparties.name.tdisplay", textEnabled);
        c.addBooleanEntry("config.sedparties.name.tshadow", textShadow);
        c.addBooleanEntry("config.sedparties.name.tattached", textAttached);
        c.addSliderEntry("config.sedparties.name.xtpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX), textX);
        c.addSliderEntry("config.sedparties.name.ytpos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int)(minecraft.font.lineHeight*scale)), textY);

        c.addTitleEntry("config.sedparties.title.textc");
        c.addColorEntry("config.sedparties.name.tcolor", color);
        c.addColorEntry("config.sedparties.name.tcabsorb", absorbColor);
        c.addColorEntry("config.sedparties.name.tcdead", deadColor);

        c.addSpaceEntry();
        c.addTitleEntry("config.sedparties.title.barc");
        c.addSpaceEntry();

        c.addTitleEntry("config.sedparties.title.barb");
        c.addColorEntry("config.sedparties.name.bbct", bColorTop);
        c.addColorEntry("config.sedparties.name.bbcb", bColorBot);
        c.addSpaceEntry();


        c.addTitleEntry("config.sedparties.title.bara");
        c.addColorEntry("config.sedparties.name.bbact", bAColorTop);
        c.addColorEntry("config.sedparties.name.bbacb", bAColorBot);
        c.addSpaceEntry();

        c.addTitleEntry("config.sedparties.title.bc");
        c.addColorEntry("config.sedparties.name.bct", colorTop);
        c.addColorEntry("config.sedparties.name.bcb", colorBot);
        c.addSpaceEntry();

        c.addTitleEntry("config.sedparties.title.bcm");
        c.addColorEntry("config.sedparties.name.bctm", colorTopMissing);
        c.addColorEntry("config.sedparties.name.bcbm", colorBotMissing);
        c.addSpaceEntry();

        c.addTitleEntry("config.sedparties.title.bca");
        c.addColorEntry("config.sedparties.name.bcta", colorTopAbsorb);
        c.addColorEntry("config.sedparties.name.bcba", colorBotAbsorb);
        c.addSpaceEntry();

        c.addTitleEntry("config.sedparties.title.baa");
        c.addColorEntry("config.sedparties.name.bcat", colorAbsTop);
        c.addColorEntry("config.sedparties.name.bcab", colorAbsBot);
        c.addSpaceEntry();

        c.addTitleEntry("config.sedparties.title.bai");
        c.addColorEntry("config.sedparties.name.bcit", colorIncTop);
        c.addColorEntry("config.sedparties.name.bcib", colorIncBot);
        c.addSpaceEntry();


        c.addTitleEntry("config.sedparties.title.bad");
        c.addColorEntry("config.sedparties.name.bcdt", colorDecTop);
        c.addColorEntry("config.sedparties.name.bcdb", colorDecBot);
        c.addSpaceEntry();

        return c;
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
}
