package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.api.mod.spellsandshields.SSCompatManager;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.ManaSSAnim;
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

public class PManaSS extends OverflowBarBase {


    public PManaSS(String name) {
        super(name, new TranslatableComponent("ui.sedparties.tooltip.mana"));
    }

    @Override
    protected void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+7, b.y+5, 0, 14, 7, bColorTop, bColorBot);
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+6, 0, 12, 5, colorTop, colorBot);
        setup(partyPath);
        RenderSystem.enableDepthTest();
        blit(poseStack,b.x+3, b.y+4, 9, 0, 9, 9);
    }

    @Override
    protected void renderSelfBar(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                 float partialTicks) {
        id.getManaSS(mana -> {
            if (id.isDead) {
                if (iconEnabled) {
                    RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorBot, bColorBot);
                    RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing);
                }
                return;
            }
            if (iconEnabled) {
                renderHealth(i, poseStack, mana);
                if (mana.active)
                    renderHealthAnim(i, poseStack, mana, partialTicks);



                //Dimmer
                RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 255 - id.alphaI << 24);
            }
            if (textEnabled)
                if (mana.absorb > 0) {
                    textCentered(tX(i), tY(i), gui, poseStack, mana.displayText, absorbColor);
                } else {
                    textCentered(tX(i), tY(i), gui, poseStack, mana.displayText, color);
                }
        });


    }

    @Override
    protected void renderSelfIcon(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                  float partialTicks) {
        id.getManaSS(mana -> {
            if (iconEnabled) {
                setup(partyPath);
                blit(poseStack,x(i), y(i), 9, 0, 9, 9);
            }

            if (textEnabled)
                if (mana.absorb > 0) {
                    text(tXI(i), tYI(i), gui, poseStack, mana.displayText, absorbColor);
                } else {
                    text(tXI(i), tYI(i), gui, poseStack, mana.displayText, color);
                }
        });

    }

    private void renderHealth(int i, PoseStack poseStack, ManaSSAnim mana) {

        float hB, aB;
        hB = mana.getPercent();
        if (mana.absorb > 0) {
            RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bAColorTop, bAColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            aB = hB + mana.getPercentA();
            rectRNoA(poseStack, i, hB, colorTop, colorBot); //Health
            rectB(poseStack, i, hB, aB, colorTopAbsorb, colorBotAbsorb); //Absorb
        } else {
            RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorTop, bColorBot);
            RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
            rectRNoA(poseStack, i, hB, colorTop, colorBot); //Health
        }
    }

    private void renderHealthAnim(int i, PoseStack poseStack, ManaSSAnim mana, float partialTicks) {
        if (mana.animTime - partialTicks < 10) {
            mana.oldH += (mana.curH - mana.oldH) * animPos(10 - mana.animTime, partialTicks, true, 10, 1);
            mana.oldA += (mana.curA - mana.oldA) * animPos(10 - mana.animTime, partialTicks, true, 10, 1);
        }

        if (mana.hInc) {
            if (mana.effHOld())
                rectAnim(poseStack, i, mana.oldH, mana.curH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, mana.oldH, mana.curH, colorIncTop, colorIncBot);

        } else {
            if (mana.effH())
                rectAnim(poseStack, i, mana.curH, mana.oldH, colorAbsTop, colorAbsBot);
            else
                rectAnim(poseStack, i, mana.curH, mana.oldH, colorDecTop, colorDecBot);

        }

        if (mana.aInc)
            rectAnim(poseStack, i, mana.oldA, mana.curA, colorAbsTop, colorAbsBot);
        else
            rectAnim(poseStack, i, mana.curA, mana.oldA, colorAbsTop, colorAbsBot);

    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator) {
                p.getManaSS(h -> {
                    renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + DF.format(h.cur + h.absorb) + "/" + DF.format(h.max), 0x66BFFF, 0x005591, 0xBEE4FF);
                });

            }
        });
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", false, 1);
        e.addEntry("barmode", true, 1);
        e.addEntry("scale", 1, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 36, 12);
        e.addEntry("width", 240, 12);
        e.addEntry("height", 12, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("ttype", 0, 4);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        e.addEntry("bhue", 56, 7);
        e.addEntry("ohue", 75, 7);
        e.addEntry("bcit", 0x9ccaff, 24);
        e.addEntry("bcib", 0x6cb0ff, 24);
        e.addEntry("bcdt", 0x126b9b, 24);
        e.addEntry("bcdb", 0xc496a, 24);
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
        c.addSliderEntry("ttype", 0, () -> 3, ManaSSAnim.type);
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
        return ManaSSAnim.setTextType(d);
    }

    @Override
    public int getTextType() {
        return ManaSSAnim.getTextType();
    }

    @Override
    public boolean isEnabled() {
        return elementEnabled && SSCompatManager.active();
    }
}
