package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.ArrayList;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class POffline extends RenderIconTextItem {

    public POffline(String name) {
        super(name);
        width = 10;
        height = 8;
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + 12;
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }

    @Override
    int getColor() {
        return 0x5d6166;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        setup(GUI_ICONS_LOCATION);
        blit(poseStack, b.x+11, b.y+7, 0, 216, 10, 8);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (!id.isOnline) {
            if (iconEnabled) {
                setup(Gui.GUI_ICONS_LOCATION);
                blit(poseStack, x(i), y(i), 0, 216, 10, 8);
            }
            if (textEnabled)
                text(gui, poseStack, "§oOffline...", tX(i), tY(i), color);
        }
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
        c.addSliderEntry("xpos", 0, () -> frameEleW - (int) (width * scale), this.x);
        c.addSliderEntry("ypos", 0, () -> frameEleH - (int) (height * scale), this.y);
        c.addTitleEntry("text");
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("tshadow", textShadow);
        c.addColorEntry("tcolor", color);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("xtpos", 0, () -> frameEleW, textX));
        entries.add(c.addSliderEntry("ytpos", 0, () -> Math.max(0, frameEleH - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        return c;
    }

    @Override
    void setDefaults() {
        OverlayRegistry.enableOverlay(item, true);
        scale = 1f;
        zPos = 0;
        iconEnabled = true;
        x = 156;
        y = 8;
        textEnabled = true;
        textShadow = false;
        color = 0xddf3ff;
        textAttached = false;
        textX = 86;
        textY = 20;
    }

}
