package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.Parties;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;

public abstract class RenderItem {

    static final ResourceLocation partyPath = new ResourceLocation(Parties.MODID, "textures/partyicons.png");
    static final ResourceLocation worldPath = new ResourceLocation(Parties.MODID, "textures/worldicons.png");

    public static int frameX = 16;
    public static int frameY = 16;
    public static int frameH = 56;
    public static int frameW = 0;

    int x, y, width, height, l, r, t, b;
    float scale;
    //TODO: Allow alpha changes in config per item?
    float alpha;

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

    int x(int pOffset) {
        return x + wOffset(pOffset);
    }

    int y(int pOffset) {
        return y + hOffset(pOffset);
    }

    int l(int pOffset) {
        return l + wOffset(pOffset);
    }

    int r(int pOffset) {
        return r + wOffset(pOffset);
    }

    int t(int pOffset) {
        return t + hOffset(pOffset);
    }

    int b(int pOffset) {
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
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, loc);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }


    static void setColor(float r, float g, float b, float a) {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor, int endColor, int alpha) {
        GuiUtils.drawGradientRect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor | (alpha << 24), endColor | (alpha << 24));
    }

    void rect(int i, PoseStack pose, int z, int offset, int startColor, int endColor) {
        GuiUtils.drawGradientRect(pose.last().pose(), z, l(i)+offset, t(i)+offset, r(i)-offset, b(i)-offset, startColor, endColor);
    }

    void rect(int i, PoseStack pose, int z, int offset, int l, int r, int startColor, int endColor) {
        GuiUtils.drawGradientRect(pose.last().pose(), z, l+offset, t(i)+offset, r-offset, b(i)-offset, startColor, endColor);

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
}
