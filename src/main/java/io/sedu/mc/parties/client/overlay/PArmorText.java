package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PArmorText extends RenderSelfItem {

    int color;

    public PArmorText(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    int getColor() {
        return 0xb8b9c4;
    }

    @Override
    public String getType() {
        return "Text";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        poseStack.scale(2f,2f,0);
        poseStack.translate(-.5f, 1, 0);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack, (b.x>>1)+4, b.y>>1, 34, 9, 9, 9);
        poseStack.popPose();
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        text(i, gui, poseStack, String.valueOf(id.getArmor()), color);
    }
}
