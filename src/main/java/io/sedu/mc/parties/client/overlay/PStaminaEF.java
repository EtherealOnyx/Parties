package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.api.epicfight.EFCompatManager;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.StaminAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static io.sedu.mc.parties.util.AnimUtils.animPos;

public class PStaminaEF extends BarBase {

    public PStaminaEF(String name) {
        super(name, new TranslatableComponent("ui.sedparties.tooltip.stamina"));
    }

    @Override
    protected void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+7, b.y+5, 0, 14, 7, bColorTop, bColorBot);
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+6, 0, 12, 5, colorTop, colorBot);
        setup(partyPath);
        RenderSystem.enableDepthTest();
        blit(poseStack,b.x+3, b.y+4, 18, 0, 9, 9);
    }

    @Override
    protected void renderSelfBar(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                 float partialTicks) {
        id.getStaminaEF(staminAnim -> {
            if (id.isDead) {
                if (iconEnabled) {
                    RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorBot, bColorBot);
                    RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing);
                }
                return;
            }
            if (iconEnabled) {
                renderStam(i, poseStack, staminAnim);
                if (staminAnim.active)
                    renderStamAnim(i, poseStack, staminAnim, partialTicks);



                //Dimmer
                RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 255 - id.alphaI << 24);
            }
            if (textEnabled)
                textCentered(tX(i), tY(i), gui, poseStack, staminAnim.stamText, color);
        });
    }

    @Override
    protected void renderSelfIcon(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                                  float partialTicks) {
        id.getStaminaEF(stam -> {
            if (iconEnabled) {
                setup(partyPath);
                blit(poseStack,x(i), y(i), 18, 0, 9, 9);
            }

            if (textEnabled)
                text(tXI(i), tYI(i), gui, poseStack, stam.stamText, color);
        });
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator) {
                p.getStaminaEF(h -> renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + (h.cur) + "/" + h.max, 0xF1E786, 0xB8AC39, 0xFFFBB8));
            }
        });
    }

    private void renderStam(int i, PoseStack poseStack, StaminAnim stam) {
        float hB;
        hB = stam.getPercent();
        RenderUtils.sizeRectNoA(poseStack.last().pose(), x(i), y(i), zPos, width, height, bColorTop, bColorBot);
        RenderUtils.offRectNoA(poseStack.last().pose(), x(i), y(i), zPos, 1, width, height, colorTopMissing, colorBotMissing); //Missing
        rectRNoA(poseStack, i, hB, colorTop, colorBot); //Mana
    }

    private void renderStamAnim(int i, PoseStack poseStack, StaminAnim stam, float partialTicks) {
        if (stam.animTime - partialTicks < 10) {
            stam.oldH += (stam.curH - stam.oldH) * animPos(10 - stam.animTime, partialTicks, true, 10, 1);
        }

        if (stam.hInc) {
            rectAnim(poseStack, i, stam.oldH, stam.curH, colorIncTop, colorIncBot);
        } else {
            rectAnim(poseStack, i, stam.curH, stam.oldH, colorDecTop, colorDecBot);
        }
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
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
        c.addSliderEntry("ttype", 0, () -> 2, StaminAnim.type);
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
        return StaminAnim.setTextType(d);
    }

    @Override
    public int getTextType() {
        return StaminAnim.getTextType();
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
        e.addEntry("ypos", 35, 12);
        e.addEntry("width", 120, 12);
        e.addEntry("height", 12, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("ttype", 0, 4);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        e.addEntry("bhue", 45, 7);
        e.addEntry("bcit", 0xaff6d6, 24);
        e.addEntry("bcib", 0x54e5a4, 24);
        e.addEntry("bcdt", 0x66ce9f, 24);
        e.addEntry("bcdb", 0x3d9d72, 24);
        return e;
    }

    @Override
    public boolean isEnabled() {
        return elementEnabled && EFCompatManager.active();
    }
}
