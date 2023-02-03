package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import java.util.List;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.*;

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
        poseStack.pushPose();
        poseStack.scale(.25f, .25f, .25f);
        rectScaled(pI, poseStack, 0, -1, ((id.dim.color & 0xfefefe) >> 1) | id.alphaI << 24, (id.dim.color) | id.alphaI << 24, 4f);
        setup(worldPath);
        useAlpha(id.alpha);
        blit(poseStack, x(pI)*4, y(pI)*4, 32*id.dim.dimension, 0, 32, 32);
        poseStack.popPose();
        resetColor();
        //Tooltip Render
        if (isActive() && withinBounds(x(pI), y(pI), x(pI)+8, y(pI)+8, 4)) {
            renderTooltip(poseStack, gui, 10, 0, id.dim.dimNorm, (id.dim.color & 0xfefefe) >> 1, id.dim.color, 0, (id.dim.color & 0xfefefe) >> 1, id.dim.color);
        }
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
        if (alphaOld > 0f) {
            setColor(1f,1f,1f,alphaOld);
            rectScaled(pI, poseStack, 0, -1, ((id.dim.oldColor & 0xfefefe) >> 1) | (int)(255*alphaOld) << 24, id.dim.oldColor | (int)(255*alphaOld) << 24 , 4 - scale);
            setup(worldPath);
            blit(poseStack, (int) (x(pI)*(4-scale)), (int) (y(pI)*(4-scale)), 32*id.dim.oldDimension, 0, 32, 32);
        }

        if (alphaNew > 0f) {
            setColor(1f,1f,1f,alphaNew);
            rectScaled(pI, poseStack, 0, -1, ((id.dim.color & 0xfefefe) >> 1) | (int)(255*alphaNew) << 24, id.dim.color | (int)(255*alphaNew) << 24 , 4 - scale);
            setup(worldPath);
            blit(poseStack, (int) (x(pI)*(4-scale)), (int) (y(pI)*(4-scale)), 32*id.dim.dimension, 0, 32, 32);

        }

        //rectScaled(pI, poseStack, 0, -1, ((id.dimColor & 0xfefefe) >> 1) | id.alphaI << 24, (id.dimColor) | id.alphaI << 24, 4 - scale);
        //setWorldShader(poseStack);
        //useAlpha(id.alpha);
        //blit(poseStack, (int) (x(pI)*(4-scale)), (int) (y(pI)*(4-scale)), 32*id.dimension, 0, 32, 32);
        poseStack.popPose();

        if (renderText)
            doTextRender(pI, gui, poseStack, currTick, partialTicks, id.dim.dimName, id.dim.color);
        resetColor();
    }



    private void doTextRender(int partyIndex, ForgeIngameGui gui, PoseStack poseStack, int currTick, float partialTicks, List<String> name, int color) {
        int x, y;
        float transX;
        transX = 0;
        float alphaPercent = 0f;
        if (currTick > 75) {

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
            alphaPercent = (currTick - partialTicks)/5f;
            transX = -10-(10-(currTick-partialTicks))*4f;
        } else {
            transX = -10-(10-(currTick-partialTicks))*4f;
        }

        for (int j = 0; j < name.size(); j++) {
            poseStack.pushPose();
            if (j % 2 == 1)
                poseStack.translate(-transX, 0, 0);
            else
                poseStack.translate(transX, 0, 0);
            x = (int) (x(partyIndex)-gui.getFont().width(name.get(j))/2f)+18;
            y = y(partyIndex) + (j*gui.getFont().lineHeight)-18;
           if (alphaPercent > 0f) {
                gui.getFont().draw(poseStack, name.get(j), x, y, color | ((int)(255*alphaPercent) << 24));
                gui.getFont().drawShadow(poseStack, name.get(j), x, y, color | ((int)(255*alphaPercent) << 24));
            }
            poseStack.popPose();
        }
    }

}
