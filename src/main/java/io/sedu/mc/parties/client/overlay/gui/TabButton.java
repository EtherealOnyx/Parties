package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class TabButton extends Button {

    private static final Component EMPTY = new TextComponent("");
    public static final ResourceLocation BUTTON_BG = new ResourceLocation("textures/block/oak_log_top.png");
    private final TabButton.OnRender onRender;
    public Component type;

    public TabButton(int pX, int pY, int pWidth, int pHeight, OnPress pOnPress, OnTooltip pOnTooltip, TabButton.OnRender i, String type) {
        super(pX, pY, pWidth, pHeight, EMPTY, pOnPress, pOnTooltip);
        this.type = new TextComponent(type);
        onRender = i;
    }

    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        //super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        onRender(poseStack);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, pMouseX, pMouseY);
        }

    }

    public void onRender(PoseStack p) {
        this.onRender.onRender(p, this);
    }

    public interface OnRender {
        void onRender(PoseStack p, TabButton b);
    }
}
