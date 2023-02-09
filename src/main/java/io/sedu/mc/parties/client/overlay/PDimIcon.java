package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.anim.DimAnim;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;

public class PDimIcon extends RenderSelfItem {


    public PDimIcon(String name, int x, int y) {
        super(name, x, y, 32, 32);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.dim.active)  {
            worldAnim(poseStack, i, gui, id, partialTicks);
        } else {
            world(poseStack, i, gui, id);
        }
    }


    private void world(PoseStack poseStack, int pI, ForgeIngameGui gui, ClientPlayerData id) {
        DimConfig.entry(id.dim.dimension, (sprite, color) -> {
            poseStack.pushPose();
            poseStack.scale(.25f, .25f, .25f);

            rectScaled(pI, poseStack, 0, -1, ((color & 0xfefefe) >> 1) | id.alphaI << 24, color | id.alphaI << 24, 4f);

            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            RenderSystem.enableBlend();
            Gui.blit(poseStack, x(pI)<<2, y(pI)<<2, 0, 32, 32, sprite);

            poseStack.popPose();

            //Tooltip Render
            if (notEditing() && withinBounds(x(pI), y(pI), x(pI)+8, y(pI)+8, 4)) {
                renderTooltip(poseStack, gui, 10, 0, id.dim.dimNorm, (color & 0xfefefe) >> 1, color, 0, (color & 0xfefefe) >> 1, color);
            }
        });
    }



    private void worldAnim(PoseStack poseStack, int pI, ForgeIngameGui gui, ClientPlayerData id, float partialTicks) {
        //Parties.LOGGER.debug(gui.getGuiTicks());
        int currTick = 0;
        float scale = 3f;
        float scaleSlow = .75f;
        float translateX = 3;
        float translateY = -26;
        float alphaOld = 1f;
        float alphaNew = 0f;
        boolean renderText = false;
        if (id.dim.animTime > 90) {
            currTick = 10 - (id.dim.animTime - 90); //0-10
            scale = 3f*animPos(currTick, partialTicks, true, 10, 1);
            scaleSlow = .75f*animPos(currTick, partialTicks, true, 10, 1.5f);
            translateX = 3*(currTick+partialTicks)/10;
            translateY =-26*(currTick+partialTicks)/10;

        } else if (id.dim.animTime > 10) {
            currTick = id.dim.animTime - 10; //80 - 0
            renderText = true;
            alphaOld = animPos(currTick, partialTicks, false, 80, 1);
            alphaNew = 1f - alphaOld;
        } else { //10 - 0
            scale = 3f*animPos(id.dim.animTime, partialTicks, false, 10, 1);
            scaleSlow = .75f*animPos(id.dim.animTime, partialTicks, false, 10, 1.5f);
            translateX = 3*(id.dim.animTime-partialTicks)/10;
            translateY = -26*(id.dim.animTime-partialTicks)/10;
            alphaOld = 0f;
            alphaNew = 1f;
        }

        poseStack.pushPose();
        poseStack.scale(.25f+scaleSlow, .25f+scaleSlow, .25f+scaleSlow);
        poseStack.translate(translateX, translateY, 0);
        int color, x, y;
        TextureAtlasSprite sprite;
        x = (int) (x(pI)*(4-scale));
        y = (int) (y(pI)*(4-scale));
        if (alphaOld > 0f) {

            sprite = DimConfig.sprite(id.dim.oldDimension);
            color = DimConfig.color(id.dim.oldDimension);
            setColor(1f,1f,1f,alphaOld);
            rectScaled(pI, poseStack, 0, -1, ((color & 0xfefefe) >> 1) | (int)(255*alphaOld) << 24, color | (int)(255*alphaOld) << 24 , 4 - scale);

            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            RenderSystem.enableBlend();
            Gui.blit(poseStack, x, y, 0, 32, 32, sprite);

        }

        color = DimConfig.color(id.dim.dimension);
        if (alphaNew > 0f) {
            sprite = DimConfig.sprite(id.dim.dimension);
            setColor(1f,1f,1f,alphaNew);
            rectScaled(pI, poseStack, 0, -1, ((color & 0xfefefe) >> 1) | (int)(255*alphaNew) << 24, color | (int)(255*alphaNew) << 24 , 4 - scale);


            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            RenderSystem.enableBlend();
            Gui.blit(poseStack, x, y, 0, 32, 32, sprite);

        }
        poseStack.popPose();

        if (renderText)
            //TODO: Revert id.dim to id.dim.dimName and extract sounds to their own implementation
            doTextRender(pI, gui, poseStack, currTick, partialTicks, id.dim, color);
        resetColor();
    }



    private void doTextRender(int partyIndex, ForgeIngameGui gui, PoseStack poseStack, int currTick, float partialTicks, DimAnim dim, int color) {
        int x, y;
        float transX;
        transX = 0;
        float alphaPercent = 0f;
        if (currTick > 75) {
            if (!dim.soundPlayed) {
                dim.soundPlayed = true;
                Minecraft.getInstance().player.playSound(SoundEvents.UI_TOAST_IN, 1.5f, 1f);
            }

        } else if (currTick > 70) {
            currTick = (currTick-70); // 5 - 0
            alphaPercent = 1f - (currTick - partialTicks)/5f;
            transX = 10+(currTick-partialTicks)*4f;
        } else if (currTick > 10) {
            currTick = currTick - 10; // 60 - 0
            alphaPercent = 1f;
            transX = 20*((currTick-partialTicks)/60f) - 10;
        } else if (currTick > 5) {
            // 5 - 0
            if (dim.soundPlayed) {
                dim.soundPlayed = false;
                Minecraft.getInstance().player.playSound(SoundEvents.UI_TOAST_OUT, 1.5f, 1f);
            }
            alphaPercent = (currTick - partialTicks)/5f;
            transX = -10-(10-(currTick-partialTicks))*4f;
        } else {
            transX = -10-(10-(currTick-partialTicks))*4f;
        }

        for (int j = 0; j < dim.dimName.size(); j++) {
            poseStack.pushPose();
            if (j % 2 == 1)
                poseStack.translate(-transX, 0, 0);
            else
                poseStack.translate(transX, 0, 0);
            x = (int) (x(partyIndex)-gui.getFont().width(dim.dimName.get(j))/2f)+18;
            //TODO: Remove hardcoding of 11 for when scaling is allowed.
            y = y(partyIndex) - 11 + (j*gui.getFont().lineHeight) - ((gui.getFont().lineHeight*dim.dimName.size()-1)>>1);
           if (alphaPercent > 0f) {
                gui.getFont().draw(poseStack, dim.dimName.get(j), x, y, color | ((int)(255*alphaPercent) << 24));
                gui.getFont().drawShadow(poseStack, dim.dimName.get(j), x, y, color | ((int)(255*alphaPercent) << 24));
            }
            poseStack.popPose();
        }
    }

}
