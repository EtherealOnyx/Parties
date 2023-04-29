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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static io.sedu.mc.parties.client.overlay.anim.AnimHandler.DF;
import static io.sedu.mc.parties.util.AnimUtils.animPos;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PHealth extends OverflowBarBase {


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
        HealthAnim hA = id.getHealth();
        if (id.isDead) {
            if (iconEnabled) {
                RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorBot, bColorBot);
                RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing);
            }

            textCentered(tX(i), tY(i), gui, poseStack, "Dead", deadColor);
            return;
        }
        if (iconEnabled) {
            renderHealth(i, poseStack, hA);
            if (hA.active)
                renderHealthAnim(i, poseStack, hA, partialTicks);



            //Dimmer
            RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 255 - id.alphaI << 24);
        }
        if (textEnabled)
            if (hA.absorb > 0) {
                textCentered(tX(i), tY(i), gui, poseStack, hA.healthText, absorbColor);
            } else {
                textCentered(tX(i), tY(i), gui, poseStack, hA.healthText, color);
            }

    }

    @Override
    protected void renderSelfIcon(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                  float partialTicks) {
        HealthAnim hA = id.getHealth();
        if (iconEnabled) {
            setup(GUI_ICONS_LOCATION);
            float percent = hA.getPercentE();
            if (percent > .8f) {
                blit(poseStack,x(i), y(i), 16, 0, 9, 9);
                blit(poseStack,x(i), y(i), 52, 0, 9, 9);
            } else if (percent > .6f) {
                blit(poseStack,x(i), y(i), 16, 0, 9, 9);
                blit(poseStack, x(i), y(i), 61 - (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);
            } else if (percent > .2f) {
                blit(poseStack, x(i), y(i), 16, 0, 9, 9);
                if ((gui.getGuiTicks() >> 4 & 1) == 0)
                    blit(poseStack, x(i), y(i), 61, 0, 9, 9);
            } else
                blit(poseStack, x(i), y(i), 16 + (gui.getGuiTicks() >> 3 & 1)*9, 0, 9, 9);
        }

        if (textEnabled)
            if (hA.absorb > 0) {
                text(tXI(i), tYI(i), gui, poseStack, hA.healthText, absorbColor);
            } else {
                text(tXI(i), tYI(i), gui, poseStack, hA.healthText, color);
            }
    }

    private void renderHealth(int i, PoseStack poseStack, HealthAnim health) {

        float hB, aB;
        hB = health.getPercent();
        if (health.absorb > 0) {
            RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bAColorTop, bAColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            aB = hB + health.getPercentA();
            rectRNoA(poseStack, i, hB, colorTop, colorBot); //Health
            rectB(poseStack, i, hB, aB, colorTopAbsorb, colorBotAbsorb); //Absorb
        } else {
            RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorTop, bColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            rectRNoA(poseStack, i, hB, colorTop, colorBot); //Health
        }
    }

    private void renderHealthAnim(int i, PoseStack poseStack, HealthAnim health, float partialTicks) {
        if (health.animTime - partialTicks < 10) {
            health.oldH += (health.curH - health.oldH) * animPos(10 - health.animTime, partialTicks, true, 10, 1);
            health.oldA += (health.curA - health.oldA) * animPos(10 - health.animTime, partialTicks, true, 10, 1);
        }

        if (health.hInc) {
            if (health.effHOld())
                rectAnim(poseStack, i, health.oldH, health.curH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, health.oldH, health.curH, colorIncTop, colorIncBot);

        } else {
            if (health.effH())
                rectAnim(poseStack, i, health.curH, health.oldH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, health.curH, health.oldH, colorDecTop, colorDecBot);

        }

        if (health.aInc)
            rectAnim(poseStack, i, health.oldA, health.curA, colorAbsTop, colorAbsBot);
        else
            rectAnim(poseStack, i, health.curA, health.oldA, colorAbsTop, colorAbsBot);

    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator) {
                HealthAnim h = p.getHealth();
                renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + DF.format(h.cur + h.absorb) + "/" + DF.format(h.max), 0xfc807c, 0x4d110f, 0xffbfbd);
            }
        });
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("barmode", true, 1);
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





    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h,
                                                 boolean parse) {
        ConfigOptionsList c = new ConfigOptionsList(this::getColor, s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("general");
        c.addBooleanEntry("display", elementEnabled);
        c.addBooleanEntry("barmode", isBarMode());
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



    @Override
    public SmallBound setTextType(int d) {
        return HealthAnim.setTextType(d);
    }

    @Override
    public int getTextType() {
        return HealthAnim.getTextType();
    }
}
