package io.sedu.mc.parties.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.sedu.mc.parties.client.overlay.RenderItem.*;
import static net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords;
import static net.minecraft.client.renderer.entity.LivingEntityRenderer.isEntityUpsideDown;

public class RenderUtils {
    public static final Vector3f NEG = new Vector3f(-1, -1, -1);
    public static final Vector3f POS = new Vector3f(1, 1, 1);
    public static Button.OnTooltip tip(Screen s, String t) {
        return new Button.OnTooltip() {
            private final Component text = new TextComponent(t);

            public void onTooltip(Button b, PoseStack p, int mX, int mY) {
                if (b.active) {
                    s.renderTooltip(p, text, mX, mY+16);
                }

            }

            public void narrateTooltip(Consumer<Component> p_169456_) {
                p_169456_.accept(this.text);
            }
        };
    }

    public static Button.OnTooltip transTip(Screen s, TranslatableComponent t) {
        return new Button.OnTooltip() {
            private final Component text = t;

            public void onTooltip(Button b, PoseStack p, int mX, int mY) {
                if (b.active) {
                    s.renderTooltip(p, text, mX, mY+16);
                }

            }

            public void narrateTooltip(Consumer<Component> p_169456_) {
                p_169456_.accept(this.text);
            }
        };
    }

    public static Button.OnTooltip tip(SettingsScreen s, TranslatableComponent tC) {
        return new Button.OnTooltip() {
            private final Component text = tC;

            public void onTooltip(Button b, PoseStack p, int mX, int mY) {
                if (b.active)
                    s.renderTooltip(p, text, mX, mY+16);
            }

            public void narrateTooltip(Consumer<Component> p_169456_) {
                p_169456_.accept(this.text);
            }
        };
    }
    public static Button.OnTooltip tip(SettingsScreen s, MutableComponent tC) {
        return new Button.OnTooltip() {
            private final Component text = tC;

            public void onTooltip(Button b, PoseStack p, int mX, int mY) {
                if (b.active)
                    s.renderTooltip(p, text, mX, mY+16);
            }

            public void narrateTooltip(Consumer<Component> p_169456_) {
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

    public static void trueRectNoA(Matrix4f mat, float x, float y, int z, int offset, float width, int height, int startColor, int endColor)
    {
        rectNoA(mat, z, x + offset, y + offset, x - offset + width, y - offset + height, startColor, endColor);
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

    public static void offRectNoA(Matrix4f mat, float x, float y, int z, int offset, float width, float height, int startColor, int endColor)
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

    public static void offRect(Matrix4f mat, float x, float y, int z, int offset, float width, float height, int color)
    {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width-offset,    y+offset, z).color(color).endVertex();
        buffer.vertex(mat,  x+offset,    y+offset, z).color(color).endVertex();
        buffer.vertex(mat,  x+offset, y+height-offset, z).color(color).endVertex();
        buffer.vertex(mat, x+width-offset, y+height-offset, z).color(color).endVertex();
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

    public static void completeRect(Matrix4f mat, float x, float y, int z, int offset, float width, float height, int startColor, int endColor)
    {


        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width-offset,    y+offset, z).color(startColor).endVertex();
        buffer.vertex(mat,  x+offset,    y+offset, z).color(startColor).endVertex();
        buffer.vertex(mat,  x+offset, y+height-offset, z).color(endColor).endVertex();
        buffer.vertex(mat, x+width-offset, y+height-offset, z).color(endColor).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    public static void completeRect(Matrix4f mat, float x, float y, int z, int offset, int width, int height, int startColor)
    {


        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width-offset,    y+offset, z).color(startColor).endVertex();
        buffer.vertex(mat,  x+offset,    y+offset, z).color(startColor).endVertex();
        buffer.vertex(mat,  x+offset, y+height-offset, z).color(startColor).endVertex();
        buffer.vertex(mat, x+width-offset, y+height-offset, z).color(startColor).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void completeRect(Matrix4f mat, float x, float y, int z, int offset, float width, float height, float r, float g, float b, float a, float r2, float g2, float b2, float a2)
    {


        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(mat, x+width,    y, 0).color(r,g,b,a).endVertex();
        buffer.vertex(mat,  x,    y, 0).color(r,g,b,a).endVertex();
        buffer.vertex(mat,  x, y+height, 0).color(r2,g2,b2,a2).endVertex();
        buffer.vertex(mat, x+width, y+height, 0).color(r2,g2,b2,a2).endVertex();
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

    public static void horizRectNoA(Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int startColor, int endColor)
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
        buffer.vertex(mat, right,    top, zLevel).color(  endRed,   endGreen,   endBlue, 1f).endVertex();
        buffer.vertex(mat,  left,    top, zLevel).color(startRed, startGreen, startBlue, 1f).endVertex();
        buffer.vertex(mat,  left, bottom, zLevel).color(startRed, startGreen, startBlue, 1f).endVertex();
        buffer.vertex(mat, right, bottom, zLevel).color(  endRed,   endGreen,   endBlue, 1f).endVertex();
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
        bufferbuilder.vertex(l, b, 0.0D).uv(0.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, b, 0.0D).uv((float)w / 32.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, t, 0.0D).uv((float)w / 32.0F, 0).color(brightness,brightness,brightness,255).endVertex();
        bufferbuilder.vertex(l, t, 0).uv(0.0F, 0).color(brightness,brightness,brightness, 255).endVertex();
        tesselator.end();
    }

    public static void renderBg(Matrix4f mat, float index, int l, int t, int w, int h, int brightness, ResourceLocation loc) {
        RenderSystem.enableDepthTest();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, loc);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(mat, l, t+h, index).uv(0.0F, 1).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(mat,l+w, t+h, index).uv(1, 1).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(mat,l+w, t, index).uv(1, 0).color(brightness,brightness,brightness,255).endVertex();
        bufferbuilder.vertex(mat, l, t, index).uv(0.0F, 0).color(brightness,brightness,brightness, 255).endVertex();
        tesselator.end();
    }

    public static void setRenderColor(int color) {
        float startRed   = (float)(color >> 16 & 255) / 255.0F;
        float startGreen = (float)(color >>  8 & 255) / 255.0F;
        float startBlue  = (float)(color       & 255) / 255.0F;
        RenderSystem.setShaderColor(startRed, startGreen, startBlue, 1f);
    }

    public static void borderRectNoBottom(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int colorStart, int colorEnd) {
        RenderUtils.sizeRect(pose, x - thickness - offset, y - thickness - offset, 0, width + ((thickness + offset)<<1), thickness, colorStart, colorStart);
        RenderUtils.sizeRect(pose, x - thickness - offset, y - (offset), 0, thickness, height + offset, colorStart, colorEnd);
        RenderUtils.sizeRect(pose, x + width + offset, y - (offset), 0, thickness, height + offset, colorStart, colorEnd);
    }


    public static void renderClickableArea(PoseStack poseStack) {

        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++)
            clickArea.rect(i, poseStack, -2, -2, ColorUtils.getRainbowColor() | 150 << 24);
    }

    public static void renderFrame(PoseStack poseStack) {
        int index = ClientPlayerData.playerOrderedList.size()-1;
        RenderUtils.sizeRect(poseStack.last().pose(),  frameX, frameY, -2, frameEleW + framePosW*index,
                             frameEleH + framePosH*index,
                             ColorUtils.getRainbowColor() | 75 << 24);
    }

    public static void renderFrameOutline(PoseStack poseStack) {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++)
            RenderUtils.borderRect(poseStack.last().pose(), -1, 1, frameX + framePosW*i, frameY + framePosH*i,
                                   frameEleW,
                                   frameEleH, 0xFFFFFFFF);
    }

    public static void renderFullArea(PoseStack poseStack, boolean b) {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
            RenderUtils.borderRect(poseStack.last().pose(), -1, 1, frameX + framePosW*i, frameY + framePosH*i,
                                   frameEleW, frameEleH,
                                   0xFFFFFFFF);
            if (b) clickArea.rect(i, poseStack, 0, -1, ColorUtils.getRainbowColor() | 150 << 24);
        }

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

    public static void offRectNoA(Matrix4f mat, float x, float y, int z, int offset, float width, float height, int startColor)
    {
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;


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
        buffer.vertex(mat,  x+offset, y+height-offset, z).color(  startRed,   startGreen,   startBlue,1f).endVertex();
        buffer.vertex(mat, x+width-offset, y+height-offset, z).color(  startRed,   startGreen,   startBlue,1f).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }


    //Player Rendering
    public static void renderPlayerModel(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> render, AbstractClientPlayer pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {

        pMatrixStack.pushPose();
        if (pEntity.isSpectator()) //For when a mod makes a certain limb not render maybe?
            render.getModel().setAllVisible(true);
        render.getModel().attackTime = pEntity.getAttackAnim(pPartialTicks);

        boolean shouldSit = pEntity.isPassenger() && (pEntity.getVehicle() != null && pEntity.getVehicle().shouldRiderSit());
        render.getModel().riding = shouldSit;
        render.getModel().young = pEntity.isBaby();
        render.getModel().crouching = pEntity.isCrouching();
        float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
        float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit) {
            pMatrixStack.translate(0,-.4f,0f);
            if (pEntity.getVehicle() instanceof LivingEntity livingentity) {
                f = Mth.rotLerp(pPartialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
                f2 = f1 - f;
                float f3 = Mth.wrapDegrees(f2);
                if (f3 < -85.0F) {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F) {
                    f3 = 85.0F;
                }

                f = f1 - f3;
                if (f3 * f3 > 2500.0F) {
                    f += f3 * 0.2F;
                }

                f2 = f1 - f;
            }
        }

        float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        if (isEntityUpsideDown(pEntity)) {
            f6 *= -1.0F;
            f2 *= -1.0F;
        }

        if (pEntity.getPose() == Pose.SLEEPING) {
            pMatrixStack.translate(-1f, .2F, 0f);
        }

        float f7 = (float) pEntity.tickCount + pPartialTicks;
        setupPlayerRotations((AbstractClientPlayer) pEntity, pMatrixStack, f7, f, pPartialTicks);
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.scale(0.9375F, 0.9375F, 0.9375F);
        pMatrixStack.translate(0.0D, (double) -1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && pEntity.isAlive()) {
            f8 = Mth.lerp(pPartialTicks, pEntity.animationSpeedOld, pEntity.animationSpeed);
            f5 = pEntity.animationPosition - pEntity.animationSpeed * (1.0F - pPartialTicks);
            if (pEntity.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        render.getModel().prepareMobModel(pEntity, f5, f8, pPartialTicks);
        render.getModel().setupAnim(pEntity, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        ResourceLocation resourcelocation = ((AbstractClientPlayer) pEntity).getSkinTextureLocation();
        boolean flag = !pEntity.isInvisible();
        boolean flag1 = !flag && !pEntity.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(pEntity);
        RenderType rendertype;
        if (flag1) {
            rendertype =  render.getModel().renderType(resourcelocation);
            //rendertype = RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (flag) {
            rendertype =  render.getModel().renderType(resourcelocation);
        } else {
            rendertype = flag2 ? RenderType.outline(resourcelocation) : null;
        }
        if (rendertype != null) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(rendertype);
            int i = getOverlayCoords(pEntity, 0F);
            render.getModel().renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
        }

        //if (!pEntity.isSpectator()) {
            for (RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderlayer : render.layers) {
                renderlayer.render(pMatrixStack, pBuffer, pPackedLight, pEntity, f5, f8, pPartialTicks, f7, f2, f6);
            }
       // }
    }

    protected static void setupPlayerRotations(AbstractClientPlayer pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (pEntityLiving.isFallFlying()) {
            setupLivingRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
            float f1 = (float)pEntityLiving.getFallFlyingTicks() + pPartialTicks;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!pEntityLiving.isAutoSpinAttack()) {
                pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F)));
            }

            Vec3 vec3 = pEntityLiving.getViewVector(pPartialTicks);
            Vec3 vec31 = pEntityLiving.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                //pMatrixStack.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            setupLivingRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
            float f3 = pEntityLiving.isInWater() ? -90.0F - pEntityLiving.getXRot() : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(f4));
            if (pEntityLiving.isVisuallySwimming()) {
                pMatrixStack.translate(0.0D, -1.0D, (double)0.3F);
            }
        } else {
            setupLivingRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        }

    }

    protected static void setupLivingRotations(LivingEntity pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        if (pEntityLiving.isFullyFrozen()) {
            pRotationYaw += (float)(Math.cos((double)pEntityLiving.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }

        Pose pose = pEntityLiving.getPose();
        if (pose != Pose.SLEEPING) {
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - pRotationYaw));
        }

        if (pEntityLiving.deathTime > 0) {
            float f = ((float)pEntityLiving.deathTime + pPartialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(f * 90F));
        } else if (pEntityLiving.isAutoSpinAttack()) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F - pEntityLiving.getXRot()));
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(((float)pEntityLiving.tickCount + pPartialTicks) * -75.0F));
        } else if (pose == Pose.SLEEPING) {
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180F));
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(90F));
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(270.0F));
        } else if (isEntityUpsideDown(pEntityLiving)) {
            pMatrixStack.translate(0.0D, (double)(pEntityLiving.getBbHeight() + 0.1F), 0.0D);
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        }

    }

    public static void renderEntityInInventory(int pPosX, int pPosY, float iScale, int pScale, LivingEntity pLivingEntity, float partialTicks) {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        float offY = 31*iScale;
        if (pLivingEntity.isCrouching()){
            offY -= 2*iScale;
        }
        if (pLivingEntity.getPose().equals(Pose.SWIMMING)) offY -= 14*iScale;

        posestack.translate(pPosX, pPosY+offY, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0D, 0.0D, 1000);
        posestack1.scale((float)pScale, (float)pScale, (float)pScale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        posestack1.mulPose(quaternion);

        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dis = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();

        RenderSystem.runAsFancy(() -> {
            renderPlayer(dis, pLivingEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, posestack1, multibuffersource$buffersource, 15728880);
        });
        multibuffersource$buffersource.endBatch();
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    public static void renderPlayer(EntityRenderDispatcher dis, LivingEntity pEntity, double pX, double pY, double pZ,
                                    float pRotationYaw, float pPartialTicks, PoseStack pMatrixStack,
                                    MultiBufferSource pBuffer, int pPackedLight) {
        if ((pEntity instanceof AbstractClientPlayer p)) {
            PlayerRenderer entityrenderer = (PlayerRenderer) dis.getRenderer(p);
            try {
                Vec3 vec3 = entityrenderer.getRenderOffset(p, pPartialTicks);
                double d2 = pX + vec3.x();
                double d3 = pY + vec3.y();
                double d0 = pZ + vec3.z();
                pMatrixStack.pushPose();
                pMatrixStack.translate(d2, d3, d0);
                renderPlayerModel(entityrenderer, p, pRotationYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);

                pMatrixStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
                pMatrixStack.popPose();
                if (pEntity.isOnFire()) {
                    renderFlame(pMatrixStack, pBuffer, pEntity);
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering entity in world");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
                pEntity.fillCrashReportCategory(crashreportcategory);
                CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
                crashreportcategory1.setDetail("Assigned renderer", entityrenderer);
                //crashreportcategory1.setDetail("Location", CrashReportCategory.formatLocation(, pX, pY, pZ));
                crashreportcategory1.setDetail("Rotation", pRotationYaw);
                crashreportcategory1.setDetail("Delta", pPartialTicks);
                throw new ReportedException(crashreport);
            }
        }
    }

    private static void renderFlame(PoseStack pMatrixStack, MultiBufferSource pBuffer, Entity pEntity) {
        TextureAtlasSprite textureatlassprite = ModelBakery.FIRE_0.sprite();
        TextureAtlasSprite textureatlassprite1 = ModelBakery.FIRE_1.sprite();
        pMatrixStack.pushPose();
        float f = pEntity.getBbWidth() * 1.4F;
        pMatrixStack.scale(f, f, f);
        float f1 = 0.5F;
        float f3 = pEntity.getBbHeight() / f;
        float f4 = 0.0F;
        pMatrixStack.translate(0.0D, 0.0D, (double)(-0.3F + (float)((int)f3) * 0.02F));
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


}
