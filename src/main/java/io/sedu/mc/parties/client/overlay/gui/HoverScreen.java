package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;

import java.awt.*;

public class HoverScreen extends Screen {

    int key;
    public HoverScreen() {
        super(new TextComponent("Mouse Hover"));
    }

    public HoverScreen key(int key) {
        this.key = key;
        return this;
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {


        int width;
        int height;
        BoundsEntry.forEachBounded(pMouseX, pMouseY, e -> {
            //Minecraft.getInstance().font.draw(pPoseStack, )
            //RenderSystem.disableDepthTest();
            //RenderSystem.disableTexture();
            drawRectC(3,10, pPoseStack.last().pose(), 0, pMouseX, pMouseY, pMouseX+e.w(), pMouseY+e.h(), (e.getColor() & 0xfefefe) >> 1, e.getColor() );
            drawRectC(2,10, pPoseStack.last().pose(), 0, pMouseX, pMouseY, pMouseX+e.w(), pMouseY+e.h(), 0x000000, (e.getColor() & 0xfefefe) >> 1);
            Minecraft.getInstance().font.draw(pPoseStack, e.tooltip, pMouseX+10, pMouseY+1, e.getColor());
            Minecraft.getInstance().font.drawShadow(pPoseStack, e.tooltip, pMouseX+10, pMouseY+1, e.getColor());
            //renderTooltip(pPoseStack, e.getTooltip(), pMouseX, pMouseY);
        });
        /*Style style = this.minecraft.gui.getChat().getClickedComponentStyleAt((double)pMouseX, (double)pMouseY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderComponentHoverEffect(pPoseStack, style, pMouseX, pMouseY);
        }*/

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode != key) {
            Minecraft.getInstance().setScreen(null);
        }
        return true;
    }

    public static void drawRectC(int change, int shift, Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int startColor, int endColor) {
        drawRect(mat, zLevel, left-change+shift, top-change, right+change+shift, bottom+change, startColor, endColor);
    }

    public static void drawRect(Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int startColor, int endColor)
    {
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endRed     = (float)(endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float)(endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float)(endColor         & 255) / 255.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, right,    top, zLevel).color(startRed, startGreen, startBlue, 1f).endVertex();
        buffer.vertex(mat,  left,    top, zLevel).color(startRed, startGreen, startBlue, 1f).endVertex();
        buffer.vertex(mat,  left, bottom, zLevel).color(  endRed,   endGreen,   endBlue, 1f).endVertex();
        buffer.vertex(mat, right, bottom, zLevel).color(  endRed,   endGreen,   endBlue, 1f).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }





}
