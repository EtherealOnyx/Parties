package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class POffline extends RenderIconTextItem {

    public POffline(String name, int x, int y, int tX, int tY, int textColor) {
        super(name, x, y, tX, tY, textColor);
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
        RenderSystem.enableDepthTest();
        blit(poseStack, b.x+11, b.y+7, 0, 216, 10, 8);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        textAttached = false;
        if (!id.isOnline) {
            setup(Gui.GUI_ICONS_LOCATION);
            blit(poseStack, x(i), y(i), 0, 216, 10, 8);
            gui.getFont().drawShadow(poseStack, "§oOffline...", tX(i), tY(i), color);
        }
    }
}
