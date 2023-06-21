package io.sedu.mc.parties.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import io.sedu.mc.parties.api.helper.ColorAPI;
import io.sedu.mc.parties.client.overlay.PHead;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.sedu.mc.parties.client.overlay.RenderItem.*;

public class RenderUtils {
    public static final Vector3f NEG = new Vector3f(-1, -1, -1);
    public static final Vector3f POS = new Vector3f(1, 1, 1);

    public static Button.OnTooltip transTip(Screen s, TranslatableComponent t) {
        return new Button.OnTooltip() {
            private final Component text = t;
            public void onTooltip(@NotNull Button b, @NotNull PoseStack p, int mX, int mY) {
                if (b.active) {
                    s.renderTooltip(p, text, mX, mY+16);
                }

            }

            public void narrateTooltip(@NotNull Consumer<Component> p_169456_) {
                p_169456_.accept(this.text);
            }
        };
    }


    public static void sizeRectNoA(Matrix4f mat, float x, float y, int z, float width, int height, int startColor, int endColor)
    {
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endRed     = (float)(endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float)(endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float)(endColor         & 255) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width,    y, z).color(startRed, startGreen, startBlue, 1f).endVertex();
        buffer.vertex(mat,  x,    y, z).color(startRed, startGreen, startBlue, 1f).endVertex();
        buffer.vertex(mat,  x, y+height, z).color(  endRed,   endGreen,   endBlue, 1f).endVertex();
        buffer.vertex(mat, x+width, y+height, z).color(  endRed,   endGreen,   endBlue,1f).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void sizeRectNoA(Matrix4f mat, float x, float y, int width, int height, int color)
    {
        float startAlpha = 1f;
        float startRed   = (float)(color >> 16 & 255) / 255.0F;
        float startGreen = (float)(color >>  8 & 255) / 255.0F;
        float startBlue  = (float)(color       & 255) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width,    y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x,    y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x, y+height, 0).color(  startRed,   startGreen,   startBlue,startAlpha).endVertex();
        buffer.vertex(mat, x+width, y+height, 0).color(  startRed,   startGreen,   startBlue,startAlpha).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void sizeRect(Matrix4f mat, float x, float y, int z, int width, int height, int color)
    {
        float startAlpha = (float)(color >> 24 & 255) / 255.0F;
        float startRed   = (float)(color >> 16 & 255) / 255.0F;
        float startGreen = (float)(color >>  8 & 255) / 255.0F;
        float startBlue  = (float)(color       & 255) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width,    y, z).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x,    y, z).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x, y+height, z).color(  startRed,   startGreen,   startBlue,startAlpha).endVertex();
        buffer.vertex(mat, x+width, y+height, z).color(  startRed,   startGreen,   startBlue,startAlpha).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void offRectNoA(Matrix4f mat, float x, float y, int z, float offset, float width, float height, int startColor, int endColor)
    {
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;

        float endRed   = (float)(endColor >> 16 & 255) / 255.0F;
        float endGreen = (float)(endColor >>  8 & 255) / 255.0F;
        float endBlue  = (float)(endColor       & 255) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width-offset,    y+offset, z).color(startRed, startGreen, startBlue, 1f).endVertex();
        buffer.vertex(mat,  x+offset,    y+offset, z).color(startRed, startGreen, startBlue, 1f).endVertex();
        buffer.vertex(mat,  x+offset, y+height-offset, z).color(  endRed,   endGreen,   endBlue,1f).endVertex();
        buffer.vertex(mat, x+width-offset, y+height-offset, z).color(  endRed,   endGreen,   endBlue,1f).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void sizeRect(Matrix4f mat, float x, float y, int z, float width, float height, int startColor, int endColor)
    {
        float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endAlpha   = (float)(endColor   >> 24 & 255) / 255.0F;
        float endRed     = (float)(endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float)(endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float)(endColor         & 255) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width,    y, z).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x,    y, z).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x, y+height, z).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buffer.vertex(mat, x+width, y+height, z).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void grayRect(Matrix4f mat, float x, float y, int z, int offset, int width, int height, float rgb, float a, float rgb2, float a2)
    {


        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width-offset,    y+offset, z).color(rgb,rgb,rgb,a).endVertex();
        buffer.vertex(mat,  x+offset,    y+offset, z).color(rgb,rgb,rgb,a).endVertex();
        buffer.vertex(mat,  x+offset, y+height-offset, z).color(rgb2,rgb2,rgb2,a2).endVertex();
        buffer.vertex(mat, x+width-offset, y+height-offset, z).color(rgb2,rgb2,rgb2,a2).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void grayRectForHead(Matrix4f mat, float x, float y, int z, int offset, int width, float height, float rgb, float a)
    {


        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width-offset,    y+offset, z).color(rgb,rgb,rgb,a).endVertex();
        buffer.vertex(mat,  x+offset,    y+offset, z).color(rgb,rgb,rgb,a).endVertex();
        buffer.vertex(mat,  x+offset, y+height-offset, z).color(rgb,rgb,rgb,a).endVertex();
        buffer.vertex(mat, x+width-offset, y+height-offset, z).color(rgb,rgb,rgb,a).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void rect(Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int startColor, int endColor)
    {
        float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endAlpha   = (float)(endColor   >> 24 & 255) / 255.0F;
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
        buffer.vertex(mat, right,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  left, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buffer.vertex(mat, right, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void rect(Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int color)
    {
        float startAlpha = (float)(color >> 24 & 255) / 255.0F;
        float startRed   = (float)(color >> 16 & 255) / 255.0F;
        float startGreen = (float)(color >>  8 & 255) / 255.0F;
        float startBlue  = (float)(color       & 255) / 255.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, right,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  left, bottom, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat, right, bottom, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void rectNoA(Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int startColor, int endColor)
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

    public static void horizRect(Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int startColor, int endColor)
    {
        float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endAlpha   = (float)(endColor   >> 24 & 255) / 255.0F;
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
        buffer.vertex(mat, right,    top, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        buffer.vertex(mat,  left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  left, bottom, zLevel).color(  startRed, startGreen, startBlue,   startAlpha).endVertex();
        buffer.vertex(mat, right, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void borderRectNoA(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int colorStart, int colorEnd) {
        RenderUtils.sizeRectNoA(pose, x - thickness - offset, y - thickness - offset, 0, width + ((thickness + offset)<<1), thickness, colorStart, colorStart);
        RenderUtils.sizeRectNoA(pose, x - thickness - offset, y + height + offset, 0, width + ((thickness + offset)<<1), thickness, colorEnd, colorEnd);
        RenderUtils.sizeRectNoA(pose, x - thickness - offset, y - (offset), 0, thickness, height+(offset<<1), colorStart, colorEnd);
        RenderUtils.sizeRectNoA(pose, x + width + offset, y - (offset), 0, thickness, height+(offset<<1), colorStart, colorEnd);
    }

    public static void borderRectNoA(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int color) {
        RenderUtils.sizeRectNoA(pose, x - thickness - offset, y - thickness - offset, width + ((thickness + offset)<<1), thickness, color);
        RenderUtils.sizeRectNoA(pose, x - thickness - offset, y + height + offset, width + ((thickness + offset)<<1), thickness, color);
        RenderUtils.sizeRectNoA(pose, x - thickness - offset, y - (offset), thickness, height+(offset<<1), color);
        RenderUtils.sizeRectNoA(pose, x + width + offset, y - (offset), thickness, height+(offset<<1), color);
    }

    public static void borderRect(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int color) {
        RenderUtils.sizeRect(pose, x - thickness - offset, y - thickness - offset, 0, width + ((thickness + offset)<<1), thickness, color);
        RenderUtils.sizeRect(pose, x - thickness - offset, y + height + offset, 0, width + ((thickness + offset)<<1), thickness, color);
        RenderUtils.sizeRect(pose, x - thickness - offset, y - (offset), 0, thickness, height+(offset<<1), color);
        RenderUtils.sizeRect(pose, x + width + offset, y - (offset), 0, thickness, height+(offset<<1), color);
    }
    public static void borderRect(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int colorStart, int colorEnd) {
        RenderUtils.sizeRect(pose, x - thickness - offset, y - thickness - offset, 0, width + ((thickness + offset)<<1), thickness, colorStart, colorStart);
        RenderUtils.sizeRect(pose, x - thickness - offset, y + height + offset, 0, width + ((thickness + offset)<<1), thickness, colorEnd, colorEnd);
        RenderUtils.sizeRect(pose, x - thickness - offset, y - (offset), 0, thickness, height+(offset<<1), colorStart, colorEnd);
        RenderUtils.sizeRect(pose, x + width + offset, y - (offset), 0, thickness, height+(offset<<1), colorStart, colorEnd);
    }

    public static void renderBg(int l, int t, int r, int b, int w, int h, int brightness, ResourceLocation loc) {
        RenderSystem.enableDepthTest();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, loc);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(l, b, 0.0D).uv(0.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, b, 0.0D).uv((float)w / 32.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, t, 0.0D).uv((float)w / 32.0F, 0).color(brightness,brightness,brightness,255).endVertex();
        bufferbuilder.vertex(l, t, 0).uv(0.0F, 0).color(brightness,brightness,brightness, 255).endVertex();
        tesselator.end();
    }

    public static void renderBg(int z, int l, int t, int r, int b, int w, int h, int brightness, ResourceLocation loc) {
        RenderSystem.enableDepthTest();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, loc);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(l, b, z).uv(0.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, b, z).uv((float)w / 32.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, t, z).uv((float)w / 32.0F, 0).color(brightness,brightness,brightness,255).endVertex();
        bufferbuilder.vertex(l, t, z).uv(0.0F, 0).color(brightness,brightness,brightness, 255).endVertex();
        tesselator.end();
    }

    public static void setRenderColor(int color) {
        float startRed   = (float)(color >> 16 & 255) / 255.0F;
        float startGreen = (float)(color >>  8 & 255) / 255.0F;
        float startBlue  = (float)(color       & 255) / 255.0F;
        RenderSystem.setShaderColor(startRed, startGreen, startBlue, 1f);
    }


    public static void renderClickableArea(PoseStack poseStack) {
        if (renderSelfFrame) {
            poseStack.pushPose();
            //For Self
            poseStack.scale(playerScale, playerScale, 1f);
            clickArea.rect(0, poseStack, -2, -2, ColorAPI.getRainbowColor() | 150 << 24);
            poseStack.popPose();
        }
        //TODO: For other members.
    }

    public static void renderSelfFrame(PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.scale(playerScale, playerScale, 1f);
        RenderUtils.sizeRect(poseStack.last().pose(), selfFrameX, selfFrameY, -2, frameEleW,
                             frameEleH,
                             ColorAPI.getRainbowColor() | 25 << 24);
        RenderUtils.borderRectNoA(poseStack.last().pose(), -1, 1, selfFrameX, selfFrameY, frameEleW,
                                  frameEleH, ColorAPI.getRainbowColor());
        poseStack.popPose();
    }

    public static void renderPartyFrame(PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.scale(partyScale, partyScale, 1f);
        int index = HoverScreen.getPartyDisplay()-1;
        RenderUtils.sizeRect(poseStack.last().pose(), partyFrameX, partyFrameY, -2, frameEleW + framePosW*index,
                             frameEleH + framePosH*index,
                             ColorAPI.getRainbowColor() | 25 << 24);
        for (int i = 0; i <= index; i++) {
            RenderUtils.borderRectNoA(poseStack.last().pose(), -1, 1, partyFrameX, partyFrameY, frameEleW + framePosW*i,
                                      frameEleH + framePosH*i, ColorAPI.getRainbowColor());
        }

        poseStack.popPose();
    }

    public static void renderSelfFrameOutline(PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.scale(playerScale, playerScale, 1f);
        RenderUtils.borderRectNoA(poseStack.last().pose(), -1, 1, selfFrameX, selfFrameY,
                               frameEleW,
                               frameEleH, 0xFFFFFF);
        poseStack.popPose();
    }

    public static void renderPartyFrameOutline(PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.scale(partyScale, partyScale, 1f);
        int index = HoverScreen.getPartyDisplay()-1;
        RenderUtils.borderRectNoA(poseStack.last().pose(), -1, 1, partyFrameX, partyFrameY, frameEleW + framePosW*index,
                                  frameEleH + framePosH*index, 0xFFFFFF);
        poseStack.popPose();
    }

    public static List<Component> splitTooltip(String text, int splitAt) {
        ArrayList<Component> tooltip = new ArrayList<>();
        boolean isTrimming = true;
        while (isTrimming) {
            int finalPos = Math.min(splitAt, text.length());
            //Get a limited line.
            String line = text.substring(0, finalPos);
            if (finalPos != text.length()) {
                int lastWord = line.lastIndexOf(' ');
                if (lastWord != -1) {
                    String testLine = line.substring(0, lastWord);
                    if (testLine.length() > 0) {
                        line = testLine;
                        finalPos = lastWord+1;
                    }
                }
            }

            tooltip.add(new TextComponent(line));
            if (finalPos != text.length()) {
                text = text.substring(finalPos);
            } else {
                isTrimming = false;
            }
        }
        return tooltip;
    }

    public static void renderEntityInInventory(int pPosX, int pPosY, float iScale, int pScale, LivingEntity pLivingEntity, float partialTicks) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        float offY = 31*iScale;
        if (pLivingEntity.isCrouching()){
            offY -= 2*iScale;
        }
        if (pLivingEntity.getPose().equals(Pose.SWIMMING)) offY -= 14*iScale;
        PHead.modelRender.renderModel(pPosX, pPosY+offY, posestack, pScale, pLivingEntity, partialTicks);
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    public static void renderFlame(PoseStack pMatrixStack, MultiBufferSource pBuffer, Entity pEntity) {
        TextureAtlasSprite textureatlassprite = ModelBakery.FIRE_0.sprite();
        TextureAtlasSprite textureatlassprite1 = ModelBakery.FIRE_1.sprite();
        pMatrixStack.pushPose();
        float f = pEntity.getBbWidth() * 1.4F;
        pMatrixStack.scale(f, f, f);
        float f1 = 0.5F;
        float f3 = pEntity.getBbHeight() / f;
        float f4 = 0.0F;
        pMatrixStack.translate(0.0D, 0.0D, -0.3F + (float)((int)f3) * 0.02F);
        float f5 = 0.0F;
        int i = 0;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(Sheets.cutoutBlockSheet());

        for(PoseStack.Pose posestack$pose = pMatrixStack.last(); f3 > 0.0F; ++i) {
            TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
            float f6 = textureatlassprite2.getU0();
            float f7 = textureatlassprite2.getV0();
            float f8 = textureatlassprite2.getU1();
            float f9 = textureatlassprite2.getV1();
            if (i / 2 % 2 == 0) {
                float f10 = f8;
                f8 = f6;
                f6 = f10;
            }

            fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
            fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
            fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
            fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
        }

        pMatrixStack.popPose();
    }

    private static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.vertex(pMatrixEntry.pose(), pX, pY, pZ).color(255, 255, 255, 255).uv(pTexU, pTexV).overlayCoords(0, 10).uv2(240).normal(pMatrixEntry.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }


    public static void renderGuiItem(ItemStack iStack, int pX, int pY, float scale, float scalePos, int zPos,
                                     float playerScale) {
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(iStack, null, Minecraft.getInstance().player, 0);
        Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate((pX+scalePos)* playerScale, (pY+scalePos)* playerScale, zPos);
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(16.0F, 16.0F, 1F);
        posestack.scale(playerScale, playerScale, 1f);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.scale(scale,scale,1f);
        posestack1.translate(0,0,zPos);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.setupGuiFlatDiffuseLighting(RenderUtils.POS, RenderUtils.NEG);

        Minecraft.getInstance().getItemRenderer().render(iStack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }


}
