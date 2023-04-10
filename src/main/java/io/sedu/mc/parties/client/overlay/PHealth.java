package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.HealthAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.ColorUtils;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static io.sedu.mc.parties.util.AnimUtils.animPos;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PHealth extends BarBase {
    int oHue = 0;
    int absorbColor;
    int bAColorTop;
    int bAColorBot;
    int colorTopAbsorb;
    int colorBotAbsorb;
    int colorAbsTop;
    int colorAbsBot;

    public PHealth(String name) {
        super(name, new TranslatableComponent("ui.sedparties.tooltip.health"));
    }

    @Override
    protected void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+7, b.y+5, 0, 14, 7, bColorTop, bColorBot);
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+6, 0, 12, 5, colorTop, colorBot);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack,b.x+3, b.y+4, 16, 0, 9, 9);
        blit(poseStack,b.x+3, b.y+4, 52, 0, 9, 9);
    }

    @Override
    protected void renderSelfBar(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                 float partialTicks) {
        if (id.isDead) {
            if (iconEnabled) {
                RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorBot, bColorBot);
                RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing);
            }

            textCentered(tX(i), tY(i), gui, poseStack, "Dead", deadColor);
            return;
        }
        if (iconEnabled) {
            renderHealth(i, poseStack, id);
            if (id.health.active)
                renderHealthAnim(i, poseStack, id, partialTicks);



            //Dimmer
            RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 255 - id.alphaI << 24);
        }
        if (textEnabled)
            if (id.health.absorb > 0) {
                textCentered(tX(i), tY(i), gui, poseStack, id.health.healthText, absorbColor);
            } else {
                textCentered(tX(i), tY(i), gui, poseStack, id.health.healthText, color);
            }

    }

    private void renderHealth(int i, PoseStack poseStack, ClientPlayerData id) {

        float hB, aB;
        hB = id.health.getPercent();
        if (id.health.absorb > 0) {
            RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bAColorTop, bAColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            aB = hB + id.health.getPercentA();
            rectRNoA(poseStack, i, hB, colorTop, colorBot); //Health
            rectB(poseStack, i, hB, aB, colorTopAbsorb, colorBotAbsorb); //Absorb
        } else {
            RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorTop, bColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            rectRNoA(poseStack, i, hB, colorTop, colorBot); //Health
        }
    }

    private void renderHealthAnim(int i, PoseStack poseStack, ClientPlayerData id, float partialTicks) {
        if (id.health.animTime - partialTicks < 10) {
            id.health.oldH += (id.health.curH - id.health.oldH) * animPos(10 - id.health.animTime, partialTicks, true, 10, 1);
            id.health.oldA += (id.health.curA - id.health.oldA) * animPos(10 - id.health.animTime, partialTicks, true, 10, 1);
        }

        if (id.health.hInc) {
            if (id.health.effHOld())
                rectAnim(poseStack, i, id.health.oldH, id.health.curH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, id.health.oldH, id.health.curH, colorIncTop, colorIncBot);

        } else {
            if (id.health.effH())
                rectAnim(poseStack, i, id.health.curH, id.health.oldH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, id.health.curH, id.health.oldH, colorDecTop, colorDecBot);

        }

        if (id.health.aInc)
            rectAnim(poseStack, i, id.health.oldA, id.health.curA, colorAbsTop, colorAbsBot);
        else
            rectAnim(poseStack, i, id.health.curA, id.health.oldA, colorAbsTop, colorAbsBot);

    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator) {
                HealthAnim h = p.health;
                renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + (h.cur + h.absorb) + "/" + h.max, 0xfc807c, 0x4d110f, 0xffbfbd);
            }
        });
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 29, 12);
        e.addEntry("width", 120, 12);
        e.addEntry("height", 10, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("ttype", 0, 4);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        e.addEntry("bhue", 0, 7);
        e.addEntry("ohue", 11, 7);
        e.addEntry("bcit", 0xC5FFC5, 24);
        e.addEntry("bcib", 0x6CFF6C, 24);
        e.addEntry("bcdt", 0xFFC5C5, 24);
        e.addEntry("bcdb", 0xFF6C6C, 24);
        return e;
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

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h,
                                                 boolean parse) {
        ConfigOptionsList c = new ConfigOptionsList(this::getColor, s, minecraft, x, y, w, h, parse);
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
        entries.add(c.addSliderEntry("xtpos", 0, () -> Math.max(0, frameEleW), textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> Math.max(0, frameEleH - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        c.addSpaceEntry();

        c.addTitleEntry("bhue");
        c.addSliderEntry("bhue", 0, () -> 100, hue, false);
        c.addTitleEntry("ohue");
        c.addSliderEntry("ohue", 0, () -> 100, oHue, false);
        c.addTitleEntry("bai");
        c.addColorEntry("bcit", colorIncTop);
        c.addColorEntry("bcib", colorIncBot);
        c.addTitleEntry("bad");
        c.addColorEntry("bcdt", colorDecTop);
        c.addColorEntry("bcdb", colorDecBot);

        return c;
    }
}
