package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PDead extends RenderIconTextItem {

    private Renderer render;

    public PDead(String name) {
        super(name);
        width = 9;
        height = 9;
        render = (i, id, gui, poseStack, partialTicks) -> {
            if (id.isDead && iconEnabled) {
                setup(Gui.GUI_ICONS_LOCATION);
                blit(poseStack,x(i), y(i), 16 + (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);
            }
        };
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + 18;
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }

    @Override
    int getColor() {
        return 0xAAAAAA;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        setup(GUI_ICONS_LOCATION);
        blit(poseStack, b.x+8, b.y+4, 16 + (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 1, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 157, 12);
        e.addEntry("ypos", 9, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("tcolor", 0xBF6666, 24);
        e.addEntry("tattached", false, 1);
        e.addEntry("xtpos", 25, 12);
        e.addEntry("ytpos", 20, 12);
        return e;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        render.render(i,id,gui,poseStack,partialTicks);
    }

    public void updateRendererForMods() {
        render = (i, id, gui, poseStack, partialTicks) -> {
            if (id.isDead) {
                if (iconEnabled) {
                    setup(Gui.GUI_ICONS_LOCATION);
                    blit(poseStack,x(i), y(i), 16 + (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);
                }
                return;
            }
            if (id.getBleeding()) {
                if (iconEnabled) {
                    setup(Gui.GUI_ICONS_LOCATION);
                    blit(poseStack,x(i), y(i), 16, 0, 9, 9);
                    RenderItem.useAlpha((float) (.5f + Math.sin((gui.getGuiTicks() + partialTicks) / 6f) / 2f));
                    blit(poseStack,x(i), y(i), 142, 0, 9, 9);
                    RenderItem.resetColor();
                }


                if (textEnabled) {
                    textCentered(tX(i), tY(i), gui, poseStack, String.valueOf(id.getTimer()), color);
                }

            }
            if (id.getDowned()) {
                if (iconEnabled) {
                    setup(Gui.GUI_ICONS_LOCATION);
                    blit(poseStack,x(i), y(i), 16, 0, 9, 9);
                    RenderItem.useAlpha((float) (.5f + Math.sin((gui.getGuiTicks() + partialTicks) / 4f) / 2f));
                    blit(poseStack,x(i), y(i), 142, 45, 9, 9);
                    RenderItem.resetColor();
                }
                if (textEnabled) {
                    textCentered(tX(i), tY(i), gui, poseStack, String.valueOf(id.getTimer()), color);
                }

            }
        };
    }

    void textCentered(int x, int y, ForgeIngameGui gui, PoseStack p, String text, int color) {
        text((int) (x - (gui.getFont().width(text)/2f)), y, gui, p, text, color);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        renderMember(i, id, gui, poseStack, partialTicks);
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
        c.addSliderEntry("xpos", 0, this::maxX, this.x);
        c.addSliderEntry("ypos", 0, this::maxY, this.y);
        c.addTitleEntry("text");
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("tshadow", textShadow);
        c.addColorEntry("tcolor", color);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> Math.max(0, frameEleW), textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> Math.max(0, frameEleH - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        return c;

    }

    private interface Renderer {
        void render(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);
    }


}
