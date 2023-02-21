package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class POffline extends RenderIconTextItem {

    public POffline(String name, int x, int y, int tX, int tY, int textColor) {
        super(name, x, y, 10, 8, textColor, false);
        this.textX = tX;
        this.textY = tY;
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
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h);
        c.addTitleEntry("config.sedparties.title.general");
        c.addBooleanEntry("config.sedparties.name.display", isEnabled());
        c.addSliderEntry("config.sedparties.name.scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("config.sedparties.name.zpos", 0, () -> 10, zPos);
        c.addTitleEntry("config.sedparties.title.icon");
        c.addBooleanEntry("config.sedparties.name.idisplay", iconEnabled);
        c.addSliderEntry("config.sedparties.name.xpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX - (int) (width * scale)), this.x);
        c.addSliderEntry("config.sedparties.name.ypos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int) (height * scale)), this.y);
        c.addTitleEntry("config.sedparties.title.text");
        c.addBooleanEntry("config.sedparties.name.tdisplay", textEnabled);
        c.addBooleanEntry("config.sedparties.name.tshadow", textShadow);
        c.addColorEntry("config.sedparties.name.tcolor", color);
        final ArrayList<ConfigOptionsList.Entry> entries = new ArrayList<>();
        c.addBooleanEntry("config.sedparties.name.tattached", textAttached, () -> toggleTextAttach(entries));
        entries.add(c.addSliderEntry("config.sedparties.name.xtpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX), textX));
        entries.add(c.addSliderEntry("config.sedparties.name.ytpos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int)(minecraft.font.lineHeight*scale)), textY));
        toggleTextAttach(entries);
        return c;
    }
}
