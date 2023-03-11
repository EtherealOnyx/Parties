package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static io.sedu.mc.parties.client.overlay.ClientPlayerData.getOrderedPlayer;

public class PLevelBar extends RenderIconTextItem implements TooltipItem {


    public PLevelBar(String name) {
        super(name);
        height = 5;
    }



    @Override
    int getColor() {
        return 0x7efc20;
    }

    @Override
    public String getType() {
        return "Bar";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        assert Minecraft.getInstance().player != null;
        float bar = Minecraft.getInstance().player.experienceProgress;
        setup(Gui.GUI_ICONS_LOCATION);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack, b.x+2, b.y+6, 0, 64, 14, 5);
        blit(poseStack, b.x+16, b.y+6, 168, 64, 14, 5);
        int w = (int) (28*bar);
        if (w > 14) {
            blit(poseStack, b.x+2, b.y+6, 0, 69, 14, 5);
            blit(poseStack, b.x+16, b.y+6, 168, 69, w-14, 5);
        } else {
            blit(poseStack, b.x+2,b.y+6, 0, 69, w, 5);
        }
        if (w > 14) {
            blit(poseStack, b.x+2, b.y+6, 0, 69, 14, 5);
            blit(poseStack, b.x+16, b.y+6, 168, 69, w-14, 5);
        } else {
            blit(poseStack, b.x+2,b.y+6, 0, 69, w, 5);
        }
        String level = String.valueOf(Minecraft.getInstance().player.experienceLevel);
        int x = b.x + 16 - (gui.getFont().width(level)>>1);
        int y = b.y + 5;
        poseStack.translate(0,0,zPos);
        gui.getFont().draw(poseStack, level, (float)(x + 1), y, 0);
        gui.getFont().draw(poseStack, level, (float)(x - 1), (float)y, 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)(y + 1), 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)(y - 1), 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)y, 8453920);
        poseStack.translate(0,0,-zPos);

    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline) {
            renderBar(i, poseStack, id.getXpBar(), id.getXpLevel(), gui);
        }
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        //useAlpha(id.alpha);
        renderBar(i, poseStack, id.getXpBarForced(), id.getLevelForced(), gui);
    }

    void renderBar(int i, PoseStack poseStack, float bar, int level, ForgeIngameGui gui) {
        if (iconEnabled) {
            setup(Gui.GUI_ICONS_LOCATION);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            RenderSystem.enableDepthTest();
            blit(poseStack, x(i), y(i), 0, 64, width>>1, height);
            blit(poseStack, x(i)+(width>>1), y(i), 182-(width>>1), 64, width>>1, height);
            int w = (int) (width*bar);

            if (w > width>>1) {
                blit(poseStack, x(i), y(i), 0, 69, width>>1, height);
                blit(poseStack, x(i)+(width>>1), y(i), 182-(width>>1), 69, w-(width>>1), height);
            } else {
                blit(poseStack, x(i), y(i), 0, 69, w, height);
            }
        }
        if (textEnabled)
            renderText(gui, poseStack, String.valueOf(level), tX(i) - (gui.getFont().width(String.valueOf(level))>>1), tY(i), bar);
    }

    private void renderText(ForgeIngameGui g, PoseStack poseStack, String s, int x, int y, float level) {
        poseStack.translate(0,0,zPos);
        if (textShadow) {
            g.getFont().draw(poseStack, s, (float)(x + 1), (float)y, 0);
            g.getFont().draw(poseStack, s, (float)(x - 1), (float)y, 0);
            g.getFont().draw(poseStack, s, (float)x, (float)(y + 1), 0);
            g.getFont().draw(poseStack, s, (float)x, (float)(y - 1), 0);
        }
        g.getFont().draw(poseStack, s, (float)x, (float)y, 8453920);
        poseStack.translate(0,0,-zPos);
    }


    protected void renderXpTooltip(PoseStack poseStack, ForgeIngameGui gui, int mouseX, int mouseY, int offsetX, int offsetY, float bar, int level) {
        int left = mouseX+offsetX;
        int top = currentY + mouseY + offsetY;
        rectCO(poseStack, 0, -3, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+offsetX+182, currentY+mouseY+5+offsetY, 0x8ec265, 0x385e1a);
        rectCO(poseStack, 0, -2, mouseX+offsetX, currentY+mouseY+offsetY, mouseX+offsetX+182, currentY+mouseY+5+offsetY, 0x140514, 0x140514);
        setup(Gui.GUI_ICONS_LOCATION);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        blit(poseStack, mouseX+offsetX, currentY+mouseY+offsetY, 0, 64, 182, 5);
        blit(poseStack, mouseX+offsetX, currentY+mouseY+offsetY, 0, 69, (int) (182*bar), 5);
        String s = "" + level;
        int i1 = left + 91 - gui.getFont().width(s)/2;
        int j1 = top - 1;
            gui.getFont().draw(poseStack, s, (float)(i1 + 1), (float)j1, 0);
        gui.getFont().draw(poseStack, s, (float)(i1 - 1), (float)j1, 0);
        gui.getFont().draw(poseStack, s, (float)i1, (float)(j1 + 1), 0);
        gui.getFont().draw(poseStack, s, (float)i1, (float)(j1 - 1), 0);
        gui.getFont().draw(poseStack, s, (float)i1, (float)j1, 8453920);
        currentY += gui.getFont().lineHeight+offsetY+4;
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + (width>>1);
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) - 1;
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
        c.addSliderEntry("xpos", 0, () -> Math.max(0, frameEleW - (int)(width*scale)), this.x, true);
        c.addSliderEntry("ypos", 0, () -> Math.max(0, frameEleH - (int)(height*scale)), this.y, true);
        c.addSliderEntry("width", 1, this::maxW, width, true);

        c.addTitleEntry("text");
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("tshadow", textShadow);
        c.addColorEntry("tcolor", color);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> frameEleW, textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> frameEleH - (int)(minecraft.font.lineHeight*scale), textY));
        toggleTextAttach(entries);
        return c;
    }

    protected int maxW() {
        return (int) Math.min(Math.ceil(frameEleW/scale), 364);
    }

    @Override
    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
        width = Mth.clamp(width, 0, maxW());
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 4, 12);
        e.addEntry("ypos", 44, 12);
        e.addEntry("width", 40, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("tcolor", 0x80ff8b, 24);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0,12);
        return e;
    }


    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData p = getOrderedPlayer(index);
        if (isSelf(index)) {
            renderXpTooltip(poseStack, gui, mouseX, mouseY, 10, 0, p.getXpBarForced(), p.getLevelForced());
        } else {
            renderXpTooltip(poseStack, gui, mouseX, mouseY, 10, 0, p.getXpBar(), p.getXpLevel());
        }
    }
}
