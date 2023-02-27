package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.ColorUtils;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class ClickArea extends RenderItem {

    public ClickArea(String n) {
        super(n);
        clickArea = this;
        scale = 1f;
        zPos = 0;
    }

    @Override
    int getColor() {
        return ColorUtils.getRainbowColor();
    }

    @Override
    public String getType() {
        return "BG";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        RenderUtils.sizeRectNoA(poseStack.last().pose(), b.x+9, b.y+5, 0, 14, 14, ColorUtils.getRainbowColor(), ColorUtils.getRainbowColor());
    }

    protected int maxH() {
        return frameEleH - y;
    }
    protected int maxW() {
        return frameEleW - x;
    }

    @Override
    ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("xpos", 7);
        e.addEntry("ypos", 7);
        e.addEntry("width", 159);
        e.addEntry("height", 34);
        return e;
    }


    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {

    }

    @Override
    public boolean isTabRendered() {
        return true;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("display");
        c.addSliderEntry("xpos", 0, () -> Math.max(0, frameEleW - width), this.x, true);
        c.addSliderEntry("ypos", 0, () -> Math.max(0, frameEleH - height), this.y, true);
        c.addSliderEntry("width", 1, this::maxW, width, true);
        c.addSliderEntry("height", 1, this::maxH, height, true);
        return c;
    }

    @Override
    protected void itemStart(PoseStack poseStack) {
    }

    @Override
    protected void itemEnd(PoseStack poseStack) {
    }


    private void clickableOutline(PoseStack poseStack) {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++)
            RenderUtils.borderRect(poseStack.last().pose(), -1, 2, l(i), t(i), width, height, 0x88AAFFFF);
    }

    @Override
    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
        width = Mth.clamp(width, 0, maxW());
        height = Mth.clamp(height, 0, maxH());
    }


}
