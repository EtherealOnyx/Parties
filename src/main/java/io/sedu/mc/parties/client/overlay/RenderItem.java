package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.Parties;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import java.util.List;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.mouseX;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.mouseY;
import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;

public abstract class RenderItem {


    static final ResourceLocation partyPath = new ResourceLocation(Parties.MODID, "textures/partyicons.png");

    public static int frameX = 16;
    public static int frameY = 16;
    public static int frameH = 56;
    public static int frameW = 0;

    public static int currentY = 0;

    int x, y, width, height, l, r, t, b;
    float scale;
    //TODO: Allow alpha changes in config per item?
    float alpha;

    public static void resetPos() {
        currentY = 0;
    }

    void setDefaults(int x, int y) {
        this.x = x + frameX;
        this.y = y + frameY;
    }

    void setDefaults(int x, int y, int width, int height) {
        setDefaults(x, y);
        this.width = width;
        this.height = height;
        l = this.x;
        r = l + width;
        t = this.y;
        b = t + height;
    }

    int hOffset(int pOffset) {
        return pOffset*frameH;
    }

    int wOffset(int pOffset) {
        return pOffset*frameW;
    }

    public int x(int pOffset) {
        return x + wOffset(pOffset);
    }

    public int y(int pOffset) {
        return y + hOffset(pOffset);
    }

    public int l(int pOffset) {
        return l + wOffset(pOffset);
    }

    public int r(int pOffset) {
        return r + wOffset(pOffset);
    }

    public int t(int pOffset) {
        return t + hOffset(pOffset);
    }

    public int b(int pOffset) {
        return b + hOffset(pOffset);
    }

    abstract void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);

    public void initItem() {
        item = (gui, poseStack, partialTicks, width, height) -> {
            for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
                renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
            }
        };
    }

    String name;
    IIngameOverlay item;

    public RenderItem(String name) {
        this.name = name;
    }

    public RenderItem(String name, int x, int y) {
        this.name = name;
        setDefaults(x, y);
    }

    public RenderItem(String name, int x, int y, int width, int height) {
        this.name = name;
        setDefaults(x, y, width, height);
    }

    public void register() {
        initItem();
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, name, item);
    }

    public void enable() {
        OverlayRegistry.enableOverlay(item, true);
    }

    public void disable() {
        OverlayRegistry.enableOverlay(item, false);
    }

    private static void setup(float alpha) {
        setup(alpha, Gui.GUI_ICONS_LOCATION);
    }

    static void setup(float alpha, ResourceLocation loc) {
        setup(loc);
        setColor(1f, 1f, 1f, alpha);
    }
    static void setup(ResourceLocation loc) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, loc);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    static void blit(PoseStack p, int x, int y, int u, int v, int w, int h) {
        GuiUtils.drawTexturedModalRect(p, x, y, u, v, w, h, 0);
    }


    static void setColor(float r, float g, float b, float a) {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor, int endColor, int alpha) {
        drawRect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor | (alpha << 24), endColor | (alpha << 24));
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor, int endColor) {
        drawRect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor, endColor);
    }

    void rectCO(PoseStack pose, int z, int offset, int l, int t, int r, int b, int startColor, int endColor) {
        drawRectCO(pose.last().pose(), z, l+offset, t+offset, r-offset, b-offset, startColor, endColor);

    }

    void rectScaled(int i, PoseStack pose, int z, int offset, int startColor, int endColor, float scale) {
        drawRect(pose.last().pose(), z, (int) ((l(i)+offset)*scale), (int) ((t(i)+offset)*scale), (int) ((l(i)-offset)*scale)+width, (int)((t(i)-offset)*scale)+height, startColor, endColor);
    }

    public static void drawRectCO(Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int startColor, int endColor)
    {
        float startAlpha = 1f;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endAlpha   = 1f;
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

    public static void drawRect(Matrix4f mat, int zLevel, float left, float top, float right, float bottom, int startColor, int endColor)
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

    void textS(int i, ForgeIngameGui gui, PoseStack p, String text, int color) {
        text(i, gui, p, text, color);
        gui.getFont().drawShadow(p, text, x(i), y(i), color);
    }

    void text(int i, ForgeIngameGui gui, PoseStack p, String text, int color) {
        gui.getFont().draw(p, text, x(i), y(i), color);
    }

    void textCentered(int i, ForgeIngameGui gui, PoseStack p, String text, int color) {
        gui.getFont().draw(p, text, 1 + x(i) - gui.getFont().width(text)/2f, y(i), color);
        gui.getFont().drawShadow(p, text, 1 + x(i) - gui.getFont().width(text)/2f, y(i), color);
    }

    static void useAlpha(float alpha) {
        setColor(1f,1f,1f,alpha);
    }

    static void resetColor() {
        setColor(1f,1f,1f,1f);
    }

    static float animPos(int currTick, float partialTicks, boolean countingUp, int animLength, float scaleFactor) {
        return (float) (countingUp ? Math.pow((currTick+partialTicks)/animLength, scaleFactor) : Math.pow((currTick-partialTicks)/animLength, scaleFactor));
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, MutableComponent text, int outStart, int outEnd, int inStart, int inEnd, int textColor) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 400);
        rectCO(poseStack, 0, -3, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+gui.getFont().width(text)+offsetX, currentY+mouseY()+(gui.getFont().lineHeight)+offsetY, outStart, outEnd);
        rectCO(poseStack, 0, -2, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+gui.getFont().width(text)+offsetX, currentY+mouseY()+(gui.getFont().lineHeight)+offsetY, inStart, inEnd);
        gui.getFont().draw(poseStack, text, mouseX()+offsetX, currentY+mouseY()+1, textColor);
        gui.getFont().drawShadow(poseStack, text, mouseX()+offsetX, currentY+mouseY()+1, textColor);
        poseStack.popPose();

        currentY += gui.getFont().lineHeight+offsetY+8;

    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, String text, int outStart, int outEnd, int inStart, int inEnd, int textColor) {
        renderTooltip(poseStack, gui, offsetX, offsetY, new TextComponent(text), outStart, outEnd, inStart, inEnd, textColor);
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, String text, int outStart, int outEnd, int textColor) {
        renderTooltip(poseStack, gui, offsetX, offsetY, new TextComponent(text), outStart, outEnd, 0x140514, 0x140514, textColor);
    }

    protected void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, MutableComponent text, int outStart, int outEnd, int textColor) {
        renderTooltip(poseStack, gui, offsetX, offsetY, text, outStart, outEnd, 0x140514, 0x140514, textColor);
    }

    protected void renderGroupEffectTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, List<ColorComponent> text, int outStart, int outEnd, int inStart, int inEnd) {
        poseStack.pushPose();
        int max = 0;
        int y = offsetY;
        poseStack.translate(0,0,400);
        for (ColorComponent c : text) {
            gui.getFont().draw(poseStack, c.c, mouseX()+offsetX, currentY+mouseY()+1+y, c.color);
            gui.getFont().drawShadow(poseStack, c.c, mouseX()+offsetX, currentY+mouseY()+1+y, c.color);
            max = Math.max(max, gui.getFont().width(c.c));
            y += gui.getFont().lineHeight+1;
        }
        rectCO(poseStack, -1, -3, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+max+offsetX, currentY+mouseY()+y+offsetY, outStart, outEnd);
        rectCO(poseStack, -1, -2, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+max+offsetX, currentY+mouseY()+y+offsetY, inStart, inEnd);
        poseStack.popPose();
        currentY += y+8;
    }

    protected void renderSingleEffectTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, List<ColorComponent> text, int color) {
        poseStack.pushPose();
        poseStack.translate(0, 0, 400);
        int max = 0;
        int y = 0;

        ColorComponent c = text.get(0);
        gui.getFont().draw(poseStack, c.c, mouseX()+offsetX, currentY+mouseY()+1, c.color);
        gui.getFont().drawShadow(poseStack, c.c, mouseX()+offsetX, currentY+mouseY()+1, c.color);
        max = Math.max(max, gui.getFont().width(c.c));
        y += (gui.getFont().lineHeight)+offsetY+4;

        c = text.get(1);
        max = Math.max(max, gui.getFont().width(c.c));
        gui.getFont().draw(poseStack, c.c, (mouseX()+offsetX)+((max-gui.getFont().width(c.c))>>1), (currentY+mouseY()+1+y), c.color);
        gui.getFont().drawShadow(poseStack, c.c, (mouseX()+offsetX)+((max-gui.getFont().width(c.c))>>1), (currentY+mouseY()+1+y), c.color);

        y += gui.getFont().lineHeight;
        rectCO(poseStack, -1, -3, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+max+offsetX, currentY+mouseY()+y+offsetY, (color & 0xfefefe) >> 1, color);
        rectCO(poseStack, -1, -2, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+max+offsetX, currentY+mouseY()+y+offsetY, 0x140514, (color & 0xfefefe) >> 1);
        poseStack.popPose();
        currentY += y+5;
    }

    static class ColorComponent {
        static final ColorComponent EMPTY = new ColorComponent(new TextComponent(""), 0);
        MutableComponent c;
        int color;

        ColorComponent(MutableComponent c, int color) {
            this.color = color;
            this.c = c;
        }
    }


}
