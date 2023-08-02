package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PRectD extends RenderItem {


    public PRectD(String n) {
        super(n);
        zPos = 0;
        scale = 1f;
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
        RenderUtils.sizeRect(poseStack.last().pose(), b.x+8, b.y+4, 0, 8, 8, 0x66002024, 0x66002024);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        rect(i, poseStack,-2, 0, 0x44002024, 0x44002024);
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("display");
        c.addBooleanEntry("display", isEnabled());
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
    @Override
    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
        width = Mth.clamp(width, 0, maxW());
        height = Mth.clamp(height, 0, maxH());
    }

    protected int maxH() {
        return frameEleH - y;
    }
    protected int maxW() {
        return frameEleW - x;
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("xpos", 6, 12);
        e.addEntry("ypos", 6, 12);
        e.addEntry("width", 165, 12);
        e.addEntry("height", 36, 12);
        return e;
    }
}
