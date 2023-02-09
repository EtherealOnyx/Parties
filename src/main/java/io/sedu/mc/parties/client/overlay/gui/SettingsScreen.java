package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class SettingsScreen extends Screen {
    private final ResourceLocation MENU_LOC = new ResourceLocation("textures/block/black_wool.png");
    private final ResourceLocation MOD_LOC = new ResourceLocation("textures/block/polished_basalt_side.png");
    private final ResourceLocation INNER_LOC = new ResourceLocation("textures/block/deepslate_bricks.png");
    private final ResourceLocation OPTIONS_LOC = new ResourceLocation("textures/block/polished_basalt_side.png");
    private final ResourceLocation SEARCH_LOC = new ResourceLocation("textures/block/deepslate_tiles.png");
    int screenW;
    int screenH;
    int screenX = 0;
    int screenY = 0;

    int menuBoxX;
    int menuBoxY;
    int menuBoxW;
    int menuBoxH;

    int modBoxX;
    int modBoxY;
    int modBoxW;
    int modBoxH;

    int optBoxX;
    int optBoxY;
    int optBoxW;
    int optBoxH;

    int searchBoxX;
    int searchBoxY;
    int searchBoxW;
    int searchBoxH;

    protected SettingsScreen() {
        super(new TextComponent("Party Advanced Settings"));
    }

    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        //this.renderBackground(poseStack);
        //renderBg();
        renderBack(poseStack);
        renderMenuBox(poseStack);
        renderModBox(poseStack);
        renderGenBox(poseStack);
        renderOptionsBox(poseStack);
        renderSearchBox(poseStack);
        renderGuiBox(poseStack);
        super.render(poseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void renderSearchBox(PoseStack poseStack) {
        renderBg(searchBoxX, searchBoxY, searchBoxX + searchBoxW, searchBoxY + searchBoxH, searchBoxW, searchBoxH, 255, SEARCH_LOC);
    }

    private void renderOptionsBox(PoseStack poseStack) {
        renderBg(optBoxX, optBoxY, optBoxX + optBoxW, optBoxY + optBoxH, optBoxW, optBoxH, 175, OPTIONS_LOC);
    }

    private void renderModBox(PoseStack poseStack) {
        //RenderItem.drawRect(poseStack.last().pose(), 0,modBoxX - 40, modBoxY, modBoxX + modBoxW, modBoxY + modBoxH, 0x33000000, 0x33000000);
        renderBg(modBoxX, modBoxY, modBoxX + modBoxW, modBoxY + modBoxH, modBoxW, modBoxH, 175, MOD_LOC);
    }

    private void renderBack(PoseStack poseStack) {
        RenderItem.drawRectCO(poseStack.last().pose(), -2, screenX-2, screenY-2, screenX + screenW+2, screenY + screenH+2, 0, 0);
        //renderBg(screenX, screenY, screenX + screenW, screenY + screenH, screenW, screenH, 255, INNER_LOC);
    }

    private void renderGenBox(PoseStack poseStack) {
        //RenderItem.drawRect(poseStack.last().pose(), 0, screenX, screenY, screenX + 40, screenY + 40, 0x33000000, 0x33000000);
    }

    private void renderMenuBox(PoseStack poseStack) {
        //RenderItem.drawRect(poseStack.last().pose(), 0,menuBoxX, menuBoxY, menuBoxX + menuBoxW, menuBoxY + menuBoxH, 0x33000000, 0x33000000);
        renderBg(menuBoxX - 32, menuBoxY, menuBoxX + menuBoxW, menuBoxY + menuBoxH, menuBoxW + 32, menuBoxH, 255, MENU_LOC);

    }

    private void renderGuiBox(PoseStack poseStack) {
        renderBg(screenX + modBoxW, screenY + menuBoxH, screenX + screenW - optBoxW, screenY + screenH - searchBoxH, screenW - modBoxW - optBoxW, screenH - menuBoxH - searchBoxH, 110, INNER_LOC);
        //renderBg(screenX + modBoxW, screenY + menuBoxH, screenX + screenW, screenY + screenH, screenW - modBoxW, screenH - menuBoxH, 80);
        //Top Shadow
        RenderItem.drawRect(poseStack.last().pose(), 0, screenX, screenY + menuBoxH, screenX + screenW, screenY + menuBoxH+10, 0xAA000000, 0x00000000);
        //Left Shadow
        RenderItem.drawRectHorizontal(poseStack.last().pose(), 0, screenX + modBoxW, screenY + menuBoxH, screenX + modBoxW + 10, screenY + screenH - searchBoxH, 0xAA000000, 0x00000000);
        //Bottom Shadow
        RenderItem.drawRect(poseStack.last().pose(), 0, screenX, screenY + screenH - 10 - searchBoxH, screenX + screenW, screenY + screenH - searchBoxH, 0x00000000, 0xAA000000);
        //Right Shadow
        RenderItem.drawRectHorizontal(poseStack.last().pose(), 0, screenX + screenW - 10 - optBoxW, screenY + menuBoxH, screenX + screenW - optBoxW, screenY + screenH - searchBoxH, 0x00000000, 0xAA000000);
        //RenderItem.drawRect(poseStack.last().pose(), 0, screenX + modBoxW, screenY + menuBoxH, screenX + screenW - optBoxW, screenY + screenH - searchBoxH, 0x66000000, 0x66000000);
    }


    protected void init() {
        setBounds(width, height);
        //Setup Data.
        super.init();
    }

    private void setBounds(int width, int height) {
        this.screenW = Math.min(width>>1, 300);
        this.screenH = Math.min((height>>2)*3, 300);
        if (width > 720)
            screenX = (width-screenW)>>1;
        else
            screenX = width - screenW - 10;
        screenY = (height-screenH)>>1;

        menuBoxW = screenW - 32;
        menuBoxX = screenX + 32;
        menuBoxY = screenY;
        menuBoxH = Math.min(40, screenH);

        modBoxW = Math.min(32, screenW);
        modBoxX = screenX;
        modBoxY = screenY+40;
        modBoxH = screenH - 40;

        optBoxH = screenH - menuBoxH;
        optBoxW = Math.min(32, screenW);
        optBoxX = screenX + screenW - optBoxW;
        optBoxY = screenY + menuBoxH;

        searchBoxH = Math.min(24, screenH);
        searchBoxW = screenW;
        searchBoxX = screenX;
        searchBoxY = screenY + screenH - searchBoxH;

    }


    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        setBounds(pWidth, pHeight);
    }

    private void renderBg(int l, int t, int r, int b, int w, int h, int brightness, ResourceLocation loc) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, loc);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(l, b, 0.0D).uv(0.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, b, 0.0D).uv((float)w / 32.0F, (float)h / 32.0F).color(brightness,brightness,brightness, 255).endVertex();
        bufferbuilder.vertex(r, t, 0.0D).uv((float)w / 32.0F, 0).color(brightness,brightness,brightness,255).endVertex();
        bufferbuilder.vertex(l, t, 0).uv(0.0F, 0).color(brightness,brightness,brightness, 255).endVertex();
        tesselator.end();
        //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, new PoseStack()));

    }

}
