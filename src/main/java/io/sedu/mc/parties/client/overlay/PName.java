package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PName extends RenderItem {
    public static ItemStack nameTag = null;

    int color;
    int length = 16;

    public PName(String name) {
        super(name);
        width = 0;
        height = 9;//Minecraft font is height of 9.
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        text(i, gui, poseStack, truncateName(id.getName()), color);
    }

    private String truncateName(String name) {
        return name.length() < length ? name : name.substring(0, length);
    }

    @Override
    int getColor() {
        return color;
    }

    @Override
    public String getType() {
        return "Text";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        poseStack.scale(.5f,.5f,1f);
        poseStack.translate(b.x+8,b.y,0);
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(nameTag, b.x+8, b.y, 0);
        poseStack.popPose();

    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        addDisplaySettings(c);
        c.addBooleanEntry("tshadow", textShadow);
        c.addColorEntry("tcolor", color);
        c.addSliderEntry("tmax", 1, () -> 16, length);
        addPositionalSettings(c, true, true, true);
        return c;
    }

    @Override
    public void setColor(int type, int data) {
        this.color = data;
    }


    @Override
    public int getColor(int type) {
        return color;
    }

    public void setMaxTextSize(int data) {
        this.length = data;
    }


    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("tshadow", true, 1);
        e.addEntry("tcolor", 0xddf3ff, 24);
        e.addEntry("tmax", 16, 5);
        e.addEntry("xpos", 46, 12);
        e.addEntry("ypos", 9, 12);
        e.addEntry("zpos", 0, 4);
        e.addEntry("scale", 2, 2);
        return e;
    }

}
