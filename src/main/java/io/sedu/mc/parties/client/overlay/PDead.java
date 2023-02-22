package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PDead extends RenderItem {

    public PDead(String name, int x, int y) {
        super(name, x, y);
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
        poseStack.pushPose();
        poseStack.scale(2f,2f,0);
        poseStack.translate(-.5f, 1, 0);
        setup(GUI_ICONS_LOCATION);
        blit(poseStack,(b.x>>1)+4, b.y>>1, 16 + (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);
        poseStack.popPose();
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isDead) {
            setup(Gui.GUI_ICONS_LOCATION);
            blit(poseStack,x(i), y(i), 16 + (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);
        }
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        addDisplaySettings(c);
        addPositionalSettings(c, true, true, true);
        return c;
    }
}
