package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.client.config.Config;
import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.isActive;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;

public class PEffects extends RenderSelfItem {

    //Potion Effects



    public PEffects(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline) {
            renderSelf(i, id, gui, poseStack, partialTicks);
        }
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.effects.sizeAll() > 0) {
            start(poseStack, i, id.effects.sizeAll(), Config.rA(), Config.mA());
            AtomicInteger iX = new AtomicInteger();
            AtomicInteger iY = new AtomicInteger();
            if (id.effects.largerAll()) {
                id.effects.forEachAllLim((effect) -> {
                    //If we reached max per row
                    if (checkRow(iX.get())) {
                        iX.set(0);
                        iY.getAndIncrement();
                    }
                    renderEffect(effect, gui, poseStack, i, iX.get(), iY.get(), partialTicks);
                    iX.getAndIncrement();
                    resetColor();
                });
                poseStack.pushPose();
                poseStack.scale(2f,2f,2f);
                if (renderOverflow(gui, poseStack, i, iX.get(), iY.get(), partialTicks)) {

                    List<ColorComponent> lC = new ArrayList<>();
                    //poseStack.translate((mouseX()+10), (mouseY()), 0);
                    id.effects.forAllRemainder((effect) -> {

                        lC.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.colorType()));
                        //renderTooltip(poseStack, gui, 10, 0, new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.getEffect().getColor(), (effect.getEffect().getColor() & 0xfefefe) >> 1, effect.colorType());
                    });
                    renderGroupEffectTooltip(poseStack, gui, 10, 0, lC, 0x3101b8, 0x24015b, 0x150615, 0x150615);

                }
                poseStack.popPose();
            } else {
                id.effects.forEachAll((effect) -> {
                    //If we reached max per row
                    if (checkRow(iX.get())) {
                        iX.set(0);
                        iY.getAndIncrement();
                    }
                    renderEffect(effect, gui, poseStack, i, iX.get(), iY.get(), partialTicks);
                    iX.getAndIncrement();
                    resetColor();
                });
            }

            end(poseStack);
        }
    }

    boolean renderOverflow(ForgeIngameGui gui, PoseStack poseStack, int i, int iX, int iY, float partialTicks) {
        gui.getFont().draw(poseStack, "▪▪▪", rX(i, iX)+2, rY(i, iY)+3, Config.cG());
        gui.getFont().drawShadow(poseStack, "▪▪▪", rX(i, iX)+2, rY(i, iY)+3, Config.cG());
        int alpha = (int) Mth.clamp((255*(float) (.5f + Math.sin((gui.getGuiTicks() + partialTicks) / 4f) / 2f)), 10, 245);
        //TODO: Remove width hardcoding for all values here. Maybe.
        gui.getFont().draw(poseStack, "▫▫▫", rX(i, iX)+2, rY(i, iY)+3, Config.cB() | alpha << 24);
        gui.getFont().drawShadow(poseStack, "▫▫▫", rX(i, iX)+2, rY(i, iY)+3, Config.cB() | alpha << 24);
        return (isActive() && withinBounds(rX(i, iX), rY(i, iY), rX(i, iX)+13, rY(i, iY)+13, 1));
    }

    boolean checkRow(int x) {
        return x+1 > Config.rA();
    }

    void renderEffect(ClientEffect effect, ForgeIngameGui gui, PoseStack poseStack, int i, int iX, int iY, float partialTicks) {
        //BG Border
        if (effect.isInstant() && (gui.getGuiTicks() >> 3 & 1) == 0) {
            //TODO: Remove width hardcoding for all values here. Maybe.
            rectInscribedFlash(poseStack.last().pose(), 1, sX(i, iX), sY(i, iY), 26, 26, 0xFFFFFF, effect.getEffect()
                                                                                                           .getColor());
        } else {
            rectInscribed(poseStack.last().pose(), 1, sX(i, iX), sY(i, iY), 26, 26, effect.getEffect().getColor(), effect.bene());
        }

        //Texture
        TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(effect.getEffect());
        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        RenderSystem.enableBlend();
        if (effect.isDying())
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 2f) / 4f));
        else
            resetColor();
        Gui.blit(poseStack, sX(i, iX) + 4, sY(i, iY) + 4,0, 18, 18, sprite);

        //Text
        int x, y;
        if (!effect.getRomanTrimmed().equals("")) {
            x = sX(i, iX) + 24 - gui.getFont().width(effect.getRomanTrimmed());
            y = sY(i, iY) + 16;
            gui.getFont().draw(poseStack, effect.getRomanTrimmed(), x, y, 0xFFD700);
            gui.getFont().drawShadow(poseStack, effect.getRomanTrimmed(), x, y, 0xFFD700);
        }

        String secs = "§oInstant";
        int scol = 0x88888888;
        if (!effect.isInstant()) {
            x = sX(i, iX) + effect.getOffset() + 8;
            y = sY(i, iY) + 29;
            gui.getFont().draw(poseStack, effect.getDisplay(), x, y, 0xFFFFFF);
            gui.getFont().drawShadow(poseStack, effect.getDisplay(), x, y, 0xFFFFFF);
            secs = effect.getDur() + "s";
            scol = 0xFFFFFF;
        }

        if (isActive() && withinBounds(rX(i, iX), rY(i, iY), rX(i, iX)+13, rY(i, iY)+13, 1)) {
            poseStack.pushPose();
            poseStack.scale(2f,2f,2f);
            List<ColorComponent> list = new ArrayList<>();
            list.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" ").append(effect.getRoman()), effect.colorType()));
            //TODO: Support descriptions via datapacks :)
            list.add(new ColorComponent(new TextComponent(secs), scol));
            renderSingleEffectTooltip(poseStack, gui, 10, 0, list,
                                      effect.getEffect().getColor());
            poseStack.popPose();
        }
    }


    void start(PoseStack poseStack, int i, int size, int row, int max) {
        poseStack.pushPose();
        drawRect(poseStack.last().pose(), -1, x(i) - 2, (y(i) - 2),
                 x(i) + (width * Math.min(size, row) >> 1),
                 -1 + y(i) + (height * (int) Math.ceil((double) Math.min(max, size) / row) >> 1),
                 0x44002024, 0x44002024);
        poseStack.scale(.5f, .5f, .5f);
        RenderSystem.enableDepthTest();
    }

    void end(PoseStack poseStack) {
        resetColor();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }


    private void rectInscribed(Matrix4f pose, int radius, int x, int y, int width, int height, int outColor, boolean ben) {
        if (ben)
            rectC(pose, x, y, width, height, Config.cG(), Config.cG());
        else
            rectC(pose, x, y, width, height,  Config.cB(), Config.cB());
        rectC(pose, x+radius, y+radius, width-(radius*2), height-(radius*2), 0x212121, (outColor & 0xfefefe) >> 1);
    }

    private void rectInscribedFlash(Matrix4f pose, int radius, int x, int y, int width, int height, int flashColor, int outColor) {
        rectC(pose, x, y, width, height,  Config.cF(), flashColor);
        rectC(pose, x+radius, y+radius, width-(radius*2), height-(radius*2), 0x212121, (outColor & 0xfefefe) >> 1);
    }

    public static void rectC(Matrix4f mat, float x, float y, int width, int height, int startColor, int endColor)
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

    private int sX(int pI, int bI) {
        return (frameW<<1)*pI+(x<<1)+width*bI;
    }

    private int rX(int pI, int bI) {
        return frameW*pI+x+(width>>1)*bI;
    }

    private int sY(int pI, int bI) {
        return (frameH<<1)*pI+(y<<1)+height*bI;
    }

    private int rY(int pI, int bI) {
        return frameH*pI+y+(height>>1)*bI;
    }


}
