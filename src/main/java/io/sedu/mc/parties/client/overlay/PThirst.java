package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;

public class PThirst extends RenderIconTextItem implements TooltipItem {

    private TranslatableComponent tipName = new TranslatableComponent("ui.sedparties.tooltip.thirst");

    public PThirst(String name) {
        super(name);
        width = 9;
        height = 9;
    }

    @Override
    int getColor() {
        return 0x66BFFF;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        setup(partyPath);
        RenderSystem.enableDepthTest();
        blit(poseStack, b.x+8, b.y+3, 0, 9, 9, 9);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (!id.isSpectator)
            renderThirst(i, gui, poseStack, id.getThirst(), id.alpha);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline && !id.isSpectator)
            renderThirst(i, gui, poseStack, id.getThirst(), id.alpha);

    }

    void renderThirst(int i, ForgeIngameGui gui, PoseStack poseStack, int thirst, float alpha) {
        if (iconEnabled) {
            useAlpha(alpha);
            setup(partyPath);
            RenderSystem.enableDepthTest();

            if (thirst > 16) {
                blit(poseStack, x(i), y(i), 0, 9, 9, 9);
            }
            else if (thirst > 12) {
                blit(poseStack, x(i), y(i), 9 - (gui.getGuiTicks() >> 4 & 1) * 9, 9, 9, 9);
            } else if (thirst > 4) {
                blit(poseStack, x(i), y(i), 9 + (gui.getGuiTicks() >> 4 & 1) * 9, 9, 9, 9);
            } else
                blit(poseStack, x(i), y(i), 18 + (gui.getGuiTicks() >> 3 & 1)*9, 9, 9, 9);

            resetColor();
        }
        if (textEnabled)
            text(gui, poseStack, String.valueOf(thirst), tX(i), tY(i), color);
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + 11;
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("general");
        c.addBooleanEntry("display", elementEnabled);
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

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("scale", 2, 2);
        e.addEntry("zpos", 0, 4);
        e.addEntry("idisplay", true, 1);
        e.addEntry("xpos", 118, 12);
        e.addEntry("ypos", 19, 12);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("tshadow", false, 1);
        e.addEntry("tcolor", 0xddf3ff, 24);
        e.addEntry("tattached", true, 1);
        e.addEntry("xtpos", 0, 12);
        e.addEntry("ytpos", 0, 12);
        return e;
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData p;
        if ((p = ClientPlayerData.getOrderedPlayer(index)).isOnline) {
            renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, tipName.getString() + p.getThirst(), 0x66BFFF, 0x005591, 0xBEE4FF);

        }
    }

    @Override
    public boolean isEnabled() {
        return elementEnabled && (ModList.get().isLoaded("thirst") || ModList.get().isLoaded("toughasnails"));
    }
}
