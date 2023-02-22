package io.sedu.mc.parties.client.overlay;

import Util.Render;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PRectD extends RenderItem {


    public PRectD(String n, int x, int y, int w, int h) {
        super(n, x, y, w, h);
    }

    @Override
    int getColor() {
        return 0xDDFFFF;
    }

    @Override
    public String getType() {
        return "BG";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Render.sizeRectNoA(poseStack.last().pose(), b.x+9, b.y+5, 0, 14, 14, 0xd3ffff, 0x779fa9);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        rect(i, poseStack,-2, 0, 0x44002024, 0x44002024);
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h);
        c.addTitleEntry("config.sedparties.title.display");
        c.addBooleanEntry("config.sedparties.name.display", isEnabled());
        c.addSliderEntry("config.sedparties.name.xpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX - width), this.x, true);
        c.addSliderEntry("config.sedparties.name.ypos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - height), this.y, true);
        c.addSliderEntry("config.sedparties.name.width", 1, this::maxW, width, true);
        c.addSliderEntry("config.sedparties.name.height", 1, this::maxH, height, true);
        return c;
    }

    @Override
    protected void itemStart(PoseStack poseStack) {
    }

    @Override
    protected void itemEnd(PoseStack poseStack) {
    }
    @Override
    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
        width = Mth.clamp(width, 0, maxW());
        height = Mth.clamp(height, 0, maxH());
    }

    protected int maxH() {
        return Math.max(clickArea.y + clickArea.h(), frameH) - y;
    }
    protected int maxW() {
        return Math.max(clickArea.x + clickArea.w(), frameW) - x;
    }
}
