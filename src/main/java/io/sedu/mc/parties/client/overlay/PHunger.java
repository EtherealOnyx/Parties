package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.anim.ManaAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PHunger extends BarBase {

    public PHunger(String name, TranslatableComponent c) {
        super(name, c);
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

    }

    @Override
    protected void renderSelfIcon(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        int hunger = id.getHunger(i);
        if (iconEnabled) {
            useAlpha(id.alpha);
            setup(Gui.GUI_ICONS_LOCATION);
            RenderSystem.enableDepthTest();

            if (hunger > 16) {
                blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                blit(poseStack, x(i), y(i), 52, 27, 9, 9);
            }
            else if (hunger > 12) {
                blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                blit(poseStack, x(i), y(i), 61 - (gui.getGuiTicks() >> 4 & 1)*9, 27, 9, 9);
            } else if (hunger > 4) {
                blit(poseStack, x(i), y(i), 16, 27, 9, 9);
                if ((gui.getGuiTicks() >> 4 & 1) == 0)
                    blit(poseStack, x(i), y(i), 61, 27, 9, 9);
            } else
                blit(poseStack, x(i), y(i), 16 + (gui.getGuiTicks() >> 3 & 1)*9, 27, 9, 9);


            resetColor();
        }
        if (textEnabled)
            text(tXI(i), tYI(i), gui, poseStack, String.valueOf(hunger), color);
    }


    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> {
            if (p.isOnline && !p.isSpectator)
                renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + p.getHunger(index), 0xb88458, 0x613c1b, 0xffd5b0);
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
        e.addEntry("width", 20, 12);
        e.addEntry("height", 10, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", false, 1);
        e.addEntry("ttype", 1, 4);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        e.addEntry("bhue", 24, 7);
        e.addEntry("bcit", 0x9ccaff, 24);
        e.addEntry("bcib", 0x6cb0ff, 24);
        e.addEntry("bcdt", 0x6790d6, 24);
        e.addEntry("bcdb", 0x1d3b6c, 24);
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
        c.addSliderEntry("ttype", 0, () -> 2, ManaAnim.type);
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
        return null;
    }

    @Override
    public int getTextType() {
        return 0;
    }
}
