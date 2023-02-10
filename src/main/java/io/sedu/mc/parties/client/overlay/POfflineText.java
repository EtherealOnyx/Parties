package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class POfflineText extends RenderSelfItem {

    int color;

    public POfflineText(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (!id.isOnline) {
            setup(Gui.GUI_ICONS_LOCATION);
            gui.getFont().drawShadow(poseStack, "§oOffline...", x(i), y(i), color);
        }
    }

    @Override
    int getColor() {
        return 0x5d6166;
    }

    @Override
    public String getType() {
        return "Text";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack, b.x+11, b.y+7, 0, 216, 10, 8);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {

    }
}
