package Util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class Render {
    public static Button.OnTooltip tip(Screen s, String t) {
        return new Button.OnTooltip() {
            private final Component text = new TextComponent(t);

            public void onTooltip(Button b, PoseStack p, int mX, int mY) {
                if (b.active)
                    s.renderTooltip(p, text, mX, mY+16);
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


    public static void sizeRectNoA(Matrix4f mat, float x, float y, int width, int height, int startColor, int endColor)
    {
        float startAlpha = 1f;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endAlpha   = 1f;
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
        buffer.vertex(mat, x+width,    y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x,    y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x, y+height, 0).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buffer.vertex(mat, x+width, y+height, 0).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
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

    public static void sizeRect(Matrix4f mat, float x, float y, int width, int height, int color)
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
        buffer.vertex(mat, x+width,    y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x,    y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x, y+height, 0).color(  startRed,   startGreen,   startBlue,startAlpha).endVertex();
        buffer.vertex(mat, x+width, y+height, 0).color(  startRed,   startGreen,   startBlue,startAlpha).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void sizeRect(Matrix4f mat, float x, float y, int width, int height, int startColor, int endColor)
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
        buffer.vertex(mat, x+width,    y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x,    y, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.vertex(mat,  x, y+height, 0).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buffer.vertex(mat, x+width, y+height, 0).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        tessellator.end();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void borderRectNoA(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int colorStart, int colorEnd) {
        Render.sizeRectNoA(pose, x - thickness - offset, y - thickness - offset, width + ((thickness + offset)<<1), thickness, colorStart, colorStart);
        Render.sizeRectNoA(pose, x - thickness - offset, y + height + offset, width + ((thickness + offset)<<1), thickness, colorEnd, colorEnd);
        Render.sizeRectNoA(pose, x - thickness - offset, y - (offset), thickness, height+(offset<<1), colorStart, colorEnd);
        Render.sizeRectNoA(pose, x + width + offset, y - (offset), thickness, height+(offset<<1), colorStart, colorEnd);
    }

    public static void borderRectNoA(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int color) {
        Render.sizeRectNoA(pose, x - thickness - offset, y - thickness - offset, width + ((thickness + offset)<<1), thickness, color);
        Render.sizeRectNoA(pose, x - thickness - offset, y + height + offset, width + ((thickness + offset)<<1), thickness, color);
        Render.sizeRectNoA(pose, x - thickness - offset, y - (offset), thickness, height+(offset<<1), color);
        Render.sizeRectNoA(pose, x + width + offset, y - (offset), thickness, height+(offset<<1), color);
    }

    public static void borderRect(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int color) {
        Render.sizeRect(pose, x - thickness - offset, y - thickness - offset, width + ((thickness + offset)<<1), thickness, color);
        Render.sizeRect(pose, x - thickness - offset, y + height + offset, width + ((thickness + offset)<<1), thickness, color);
        Render.sizeRect(pose, x - thickness - offset, y - (offset), thickness, height+(offset<<1), color);
        Render.sizeRect(pose, x + width + offset, y - (offset), thickness, height+(offset<<1), color);
    }
    public static void borderRect(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int colorStart, int colorEnd) {
        Render.sizeRect(pose, x - thickness - offset, y - thickness - offset, width + ((thickness + offset)<<1), thickness, colorStart, colorStart);
        Render.sizeRect(pose, x - thickness - offset, y + height + offset, width + ((thickness + offset)<<1), thickness, colorEnd, colorEnd);
        Render.sizeRect(pose, x - thickness - offset, y - (offset), thickness, height+(offset<<1), colorStart, colorEnd);
        Render.sizeRect(pose, x + width + offset, y - (offset), thickness, height+(offset<<1), colorStart, colorEnd);
    }

    public static void renderBg(int l, int t, int r, int b, int w, int h, int brightness, ResourceLocation loc) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, loc);
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(l, b, 0.0D).uv(0.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, b, 0.0D).uv((float)w / 32.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, t, 0.0D).uv((float)w / 32.0F, 0).color(brightness,brightness,brightness,255).endVertex();
        bufferbuilder.vertex(l, t, 0).uv(0.0F, 0).color(brightness,brightness,brightness, 255).endVertex();
        tesselator.end();
    }

    public static void setColor(int color) {
        float startRed   = (float)(color >> 16 & 255) / 255.0F;
        float startGreen = (float)(color >>  8 & 255) / 255.0F;
        float startBlue  = (float)(color       & 255) / 255.0F;
        RenderSystem.setShaderColor(startRed, startGreen, startBlue, 1f);
    }

    public static void borderRectNoBottom(Matrix4f pose, int offset, int thickness, int x, int y, int width, int height, int colorStart, int colorEnd) {
        Render.sizeRect(pose, x - thickness - offset, y - thickness - offset, width + ((thickness + offset)<<1), thickness, colorStart, colorStart);
        //Render.sizeRect(pose, x - thickness - offset, y + width + offset, width + ((thickness + offset)<<1), thickness, colorEnd, colorEnd);
        Render.sizeRect(pose, x - thickness - offset, y - (offset), thickness, height + offset, colorStart, colorEnd);
        Render.sizeRect(pose, x + width + offset, y - (offset), thickness,  height + offset, colorStart, colorEnd);
    }
}
