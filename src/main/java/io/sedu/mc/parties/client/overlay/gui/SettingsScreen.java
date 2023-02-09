package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;

public class SettingsScreen extends Screen {
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

    protected SettingsScreen() {
        super(new TextComponent("Party Advanced Settings"));
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        //this.renderBackground(pPoseStack);
        //renderBg();
        renderGuiBox(pPoseStack);
        renderMenuBox(pPoseStack);
        renderModBox(pPoseStack);
        renderGenBox(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void renderGenBox(PoseStack poseStack) {
        RenderItem.drawRect(poseStack.last().pose(), -1, screenX, screenY, screenX + 40, screenY + 40, 0xCCAAAAFF, 0xCCAAAAFF);
    }

    private void renderModBox(PoseStack poseStack) {
        RenderItem.drawRect(poseStack.last().pose(), -1,modBoxX, modBoxY, modBoxX + modBoxW, modBoxY + modBoxH, 0xCCAAFFFF, 0xCCAAFFFF);
    }

    private void renderMenuBox(PoseStack poseStack) {
        RenderItem.drawRect(poseStack.last().pose(), -1,menuBoxX, menuBoxY, menuBoxX + menuBoxW, menuBoxY + menuBoxH, 0xCCFFAAAA, 0xCCFFAAAA);
    }

    private void renderGuiBox(PoseStack poseStack) {
        RenderItem.drawRect(poseStack.last().pose(), -1, screenX, screenY, screenX + screenW, screenY + screenH, 0xCC000000, 0xCC000000);
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

        menuBoxW = screenW - 40;
        menuBoxX = screenX + 40;
        menuBoxY = screenY;
        menuBoxH = Math.min(40, screenH);

        modBoxW = Math.min(40, screenW);
        modBoxX = screenX;
        modBoxY = screenY+40;
        modBoxH = screenH - 40;
    }


    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        setBounds(pWidth, pHeight);
    }

    private void renderBg() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(screenX, screenY + screenH, 0.0D).uv(0.0F, (float)screenH / 32.0F).color(64, 64, 64, 175).endVertex();
        bufferbuilder.vertex(screenX+screenW, screenY + screenH, 0.0D).uv((float)screenW / 32.0F, (float)screenH / 32.0F).color(64, 64, 64, 175).endVertex();
        bufferbuilder.vertex(screenX+screenW, screenY, 0.0D).uv((float)screenW / 32.0F, 0).color(64, 64, 64, 175).endVertex();
        bufferbuilder.vertex(screenX, screenY, 0).uv(0.0F, 0).color(64, 64, 64, 175).endVertex();
        tesselator.end();
        //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, new PoseStack()));

    }

}
