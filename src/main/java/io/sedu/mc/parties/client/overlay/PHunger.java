package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.HungerAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static io.sedu.mc.parties.client.overlay.anim.AnimBarHandler.DF;
import static io.sedu.mc.parties.util.AnimUtils.animPos;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PHunger extends OverflowBarBase {

    public PHunger(String name) {
        super(name, new TranslatableComponent("ui.sedparties.tooltip.hunger"));
    }

    @Override
    protected void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+7, b.y+5, 0, 14, 7, bColorTop, bColorBot);
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+6, 0, 12, 5, colorTop, colorBot);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack,b.x+3, b.y+4, 16, 27, 9, 9);
        blit(poseStack,b.x+3, b.y+4, 52, 27, 9, 9);
    }

    @Override
    protected void renderSelfBar(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        id.getHunger(hunger -> {
            if (id.isDead) {
                if (iconEnabled) {
                    RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorBot, bColorBot);
                    RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing);
                }
                return;
            }
            if (iconEnabled) {
                renderHunger(i, poseStack, hunger);
                if (hunger.active)
                    renderHungerAnim(i, poseStack, hunger, partialTicks);



                //Dimmer
                RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 255 - id.alphaI << 24);
            }
            if (textEnabled)
                if (hunger.absorb > 0) {
                    textCentered(tX(i), tY(i), gui, poseStack, hunger.displayText, absorbColor);
                } else {
                    textCentered(tX(i), tY(i), gui, poseStack, hunger.displayText, color);
                }
        });
    }

    private void renderHungerAnim(int i, PoseStack poseStack, HungerAnim hunger, float partialTicks) {
        if (hunger.animTime - partialTicks < 10) {
            hunger.oldH += (hunger.curH - hunger.oldH) * animPos(10 - hunger.animTime, partialTicks, true, 10, 1);
            hunger.oldA += (hunger.curA - hunger.oldA) * animPos(10 - hunger.animTime, partialTicks, true, 10, 1);
        }

        if (hunger.hInc) {
            if (hunger.effHOld())
                rectAnim(poseStack, i, hunger.oldH, hunger.curH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, hunger.oldH, hunger.curH, colorIncTop, colorIncBot);

        } else {
            if (hunger.effH())
                rectAnim(poseStack, i, hunger.curH, hunger.oldH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, hunger.curH, hunger.oldH, colorDecTop, colorDecBot);

        }

        if (hunger.aInc)
            rectAnim(poseStack, i, hunger.oldA, hunger.curA, colorAbsTop, colorAbsBot);
        else
            rectAnim(poseStack, i, hunger.curA, hunger.oldA, colorAbsTop, colorAbsBot);
    }

    private void renderHunger(int i, PoseStack poseStack, HungerAnim hunger) {
        float hB, aB;
        hB = hunger.getPercent();
        if (hunger.absorb > 0) {
            RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bAColorTop, bAColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            aB = hB + hunger.getPercentA();
            rectRNoA(poseStack, i, hB, colorTop, colorBot); //Health
            rectB(poseStack, i, hB, aB, colorTopAbsorb, colorBotAbsorb); //Absorb
        } else {
            RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorTop, bColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            rectRNoA(poseStack, i, hB, colorTop, colorBot); //Health
        }
    }

    @Override
    protected void renderSelfIcon(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        id.getHunger(hunger -> {
            if (iconEnabled) {
                useAlpha(id.alpha);
                setup(GUI_ICONS_LOCATION);
                RenderSystem.enableDepthTest();

                if (hunger.cur > 16) {
                    blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                    blit(poseStack, x(i), y(i), 52, 27, 9, 9);
                }
                else if (hunger.cur > 12) {
                    blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                    blit(poseStack, x(i), y(i), 61 - (gui.getGuiTicks() >> 4 & 1)*9, 27, 9, 9);
                } else if (hunger.cur > 4) {
                    blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                    if ((gui.getGuiTicks() >> 4 & 1) == 0)
                        blit(poseStack, x(i), y(i), 61, 27, 9, 9);
                } else
                    blit(poseStack, x(i), y(i), 16 + (gui.getGuiTicks() >> 3 & 1)*9, 27, 9, 9);


                resetColor();
            }
            if (textEnabled)
                if (hunger.absorb > 0) {
                    text(tXI(i), tYI(i), gui, poseStack, hunger.displayText, absorbColor);
                } else {
                    text(tXI(i), tYI(i), gui, poseStack, hunger.displayText, color);
                }
        });

    }


    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator) {
                p.getHunger(hunger -> renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + DF.format(hunger.cur + hunger.absorb) + "/" + DF.format(hunger.max), 0xb88458, 0x613c1b, 0xffd5b0));
            }

        });
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("barmode", false, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 143, 12);
        e.addEntry("ypos", 19, 12);
        e.addEntry("width", 23, 12);
        e.addEntry("height", 10, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", false, 1);
        e.addEntry("ttype", 3, 4);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        e.addEntry("bhue", 9, 7);
        e.addEntry("ohue", 11, 7);
        e.addEntry("bcit", 0xffce83, 24);
        e.addEntry("bcib", 0xbc8532, 24);
        e.addEntry("bcdt", 0x9b5e00, 24);
        e.addEntry("bcdb", 0x7e4c00, 24);
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
        c.addSliderEntry("ttype", 0, () -> 3, HungerAnim.type);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> Math.max(0, frameEleW), textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> Math.max(0, frameEleH - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        c.addSpaceEntry();
        c.addTitleEntry("bhue");
        c.addSliderEntry("bhue", 0, () -> 100, hue, false);
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
        return HungerAnim.setTextType(d);
    }

    @Override
    public int getTextType() {
        return HungerAnim.getTextType();
    }
}
