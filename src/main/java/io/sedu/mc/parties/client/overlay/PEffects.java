package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.isActive;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;

public class PEffects extends RenderSelfItem {

    //Potion Effects
    int max;
    int maxPerRow;


    public PEffects(String name, int x, int y, int width, int height, int maxEffects, int maxPerRow) {
        super(name, x, y, width, height);
        max = maxEffects;
        this.maxPerRow = maxPerRow;
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
            start(poseStack, i, id.effects.sizeAll());
            AtomicInteger iX = new AtomicInteger();
            AtomicInteger iY = new AtomicInteger();
            id.effects.forEachAll((effect) -> {
                //If we reached the limit
                if (check(iX.get(), iY.get())) {
                    return;
                }


                //If we reached max per row
                if (checkRow(iX.get())) {
                    iX.set(0);
                    iY.getAndIncrement();
                }

                renderEffect(effect, gui, poseStack, i, iX.get(), iY.get(), partialTicks);

                iX.getAndIncrement();
                resetColor();
            });
            end(poseStack);
        }
    }

    boolean check(int x, int y) {
        return x + y*maxPerRow+1 > max;
    }

    boolean checkRow(int x) {
        return x+1 > maxPerRow;
    }

    void renderEffect(ClientEffect effect, ForgeIngameGui gui, PoseStack poseStack, int i, int iX, int iY, float partialTicks) {
        //BG Border
        if (effect.isInstant() && (gui.getGuiTicks() >> 3 & 1) == 0) {
            rectInscribedFlash(poseStack.last().pose(), 1, sX(i, iX), sY(i, iY), 26, 26, effect.getEffect()
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

        if (!effect.isInstant()) {
            x = sX(i, iX) + effect.getOffset() + 8;
            y = sY(i, iY) + 29;
            gui.getFont().draw(poseStack, effect.getDisplay(), x, y, 0xFFFFFF);
            gui.getFont().drawShadow(poseStack, effect.getDisplay(), x, y, 0xFFFFFF);
        }
        //System.out.println("Hello");
        if (isActive() && withinBounds(rX(i, iX), rY(i, iY), rX(i, iX)+13, rY(i, iY)+13, 2)) {
            poseStack.scale(2f,2f,2f);
            List<ColorComponent> list = new ArrayList<>();
            list.add(new ColorComponent(new TranslatableComponent(effect.getEffect().getDescriptionId()).append(" " + effect.getRoman()), effect.getEffect().isBeneficial() ? 0xA9E5FF : 0xFFA9A9));
            //TODO: Support descriptions via datapacks :)
            list.add(ColorComponent.EMPTY);
            list.add(new ColorComponent(new TextComponent(effect.getDur() + " second(s)").withStyle(ChatFormatting.ITALIC), 0xAAAAAA));
            renderTooltip(poseStack, gui, 10, 0, list,
                          (effect.getEffect().getColor() & 0xfefefe) >> 1, effect.getEffect().getColor(),
                          0x140514, (effect.getEffect().getColor() & 0xfefefe) >> 1);
            poseStack.scale(.5f,.5f,.5f);
        }
    }


    void start(PoseStack poseStack, int i, int size) {
        poseStack.pushPose();
        drawRect(poseStack.last().pose(), -1, x(i) - 2, (y(i) - 2),
                 x(i) + (width * Math.min(size, maxPerRow) >> 1),
                 -1 + y(i) + (height * (int) Math.ceil((double) Math.min(max, size) / maxPerRow) >> 1),
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
            rectC(pose, x, y, width, height, 0xA9E5FF, 0x74E5FF);
        else
            rectC(pose, x, y, width, height,  0xFFA9A9, 0xFF7474);
        rectC(pose, x+radius, y+radius, width-(radius*2), height-(radius*2), 0x212121, (outColor & 0xfefefe) >> 1);
    }

    private void rectInscribedFlash(Matrix4f pose, int radius, int x, int y, int width, int height, int outColor) {
        rectC(pose, x, y, width, height,  0xFFFFFF, 0xFFFFFF);
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
