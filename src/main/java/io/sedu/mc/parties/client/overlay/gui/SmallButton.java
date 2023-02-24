package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;

public class SmallButton extends Button {

    private float startAlpha = 1f;
    private float r = 1f;
    private float g = 1f;
    private float b = 1f;
    private int offX = 0;
    private int offY = 0;

    public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip, float r, float g, float b) {
        super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
        this.r = r;
        this.g = g;
        this.b = b;

    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip, float r, float g, float b, float a) {
        super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
        this.r = r;
        this.g = g;
        this.b = b;
        this.startAlpha = a;
    }

    public SmallButton(int pX, int pY, String m, OnPress pOnPress, OnTooltip pOnTooltip, int offX, int offY, float r, float g, float b) {
        super(pX, pY, 10,10, new TextComponent(m), pOnPress, pOnTooltip);
        this.offX = offX;
        this.offY = offY;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public SmallButton(int pX, int pY, int w, String m, OnPress pOnPress, OnTooltip pOnTooltip, int offX, int offY, float r, float g, float b, float a) {
        super(pX, pY, w, 10, new TextComponent(m), pOnPress, pOnTooltip);
        this.offX = offX;
        this.offY = offY;
        this.r = r;
        this.g = g;
        this.b = b;
        this.startAlpha = a;
    }

    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        pPoseStack.pushPose();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);

        int i;
        if (this.isHoveredOrFocused()) {
            i = this.getYImage(true);
            RenderSystem.setShaderColor(r, g, b, 1f);
        } else {
            i = this.getYImage(false);
            RenderSystem.setShaderColor(r, g, b, startAlpha);
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        pPoseStack.scale(.5f,.5f,0);
        this.blit(pPoseStack, this.x<<1, this.y<<1, 0, 46 + i * 20, width, 20);
        this.blit(pPoseStack, (this.x<<1) + width, this.y<<1, 190 + (10 - width), 46 + i * 20, width, 20);

        pPoseStack.scale(2f,2f,0);
        //this.renderBg(pPoseStack, minecraft, pMouseX, pMouseY);
        int j = getFGColor();
        font.draw(pPoseStack, this.getMessage(), (float)((this.x+5+offX) - font.width(this.getMessage()) / 2), this.y+offY, j);
        //drawCenteredString(pPoseStack, font, this.getMessage(), this.x+5+offX, this.y+offY, j);
        pPoseStack.popPose();
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        if (this.isHoveredOrFocused()) {
            this.renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }


}