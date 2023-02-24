package io.sedu.mc.parties.client.overlay;

import io.sedu.mc.parties.util.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

public class PHead extends RenderItem {

    public static ItemStack icon = null;


    public PHead(String name) {
        super(name);
        PDimIcon.head = this;
        width = 32;
        height = 32;
    }

    @Override
    int getColor() {
        return 0xAACCAA;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, b.x+8, b.y+3, 0);
    }

    @Override
    void setDefaults() {
        OverlayRegistry.enableOverlay(item, true);
        x = 8;
        y = 8;
        zPos = 0;
        scale = 1f;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        RenderSystem.enableDepthTest();
        if (id.isDead)
            setColor(1f, .5f, .5f, .5f);
        else
            setColor(1f, 1f, 1f, id.alpha);
        RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, id.alpha, 0.5f, id.alpha);
        setup(id.getHead());
        blit(poseStack, x(i), y(i), 32, 32, 32, 32);
        resetColor();
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("display");
        c.addBooleanEntry("display", isEnabled());
        c.addSliderEntry("xpos", 0, () -> Math.max(0, frameEleW - frameX - (int)(width*scale)), this.x);
        c.addSliderEntry("ypos", 0, () -> Math.max(0, frameEleH - frameY - (int)(height*scale)), this.y);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addSliderEntry("scale", 1, () -> 3, getScale(), true);
        return c;
    }


}
