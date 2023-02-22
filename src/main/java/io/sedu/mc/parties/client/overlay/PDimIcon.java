package io.sedu.mc.parties.client.overlay;

import Util.Render;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.anim.DimAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;

public class PDimIcon extends RenderSelfItem {

    public static ItemStack icon = null;
    protected static PHead head = null;
    private boolean renderText = true;

    public PDimIcon(String name, int x, int y) {
        super(name, x, y, 32, 32);
    }

    @Override
    int getColor() {
        return 0x93c263;
    }

    @Override
    public String getType() {
        return "Misc";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, b.x+8, b.y+3, 0);
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
            poseStack.scale(.25f, .25f, 1f);
            poseStack.translate(0,0,10f);

            rectScaled(pI, poseStack, 0, -1, ((color & 0xfefefe) >> 1) | id.alphaI << 24, color | id.alphaI << 24, 4f/head.scale);

            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            RenderSystem.enableBlend();
            Gui.blit(poseStack, (int) ((x(pI)<<2)/head.scale), (int) ((y(pI)<<2)/head.scale), 0, 32, 32, sprite);

            poseStack.popPose();

            //Tooltip Render
            if (notEditing() && withinBounds(xNormal(pI), yNormal(pI), 8, 8, 4, scale)) {
                renderTooltip(poseStack, gui, 10, 0, id.dim.dimNorm, (color & 0xfefefe) >> 1, color, 0, (color & 0xfefefe) >> 1, color);
            }
        });

    }

    void rectScaled(int i, PoseStack pose, int z, int offset, int startColor, int endColor, float scale) {
        Render.rect(pose.last().pose(), z, ((x(i)+offset)*scale), ((y(i)+offset)*scale), ((x(i)-offset)*scale)+width, ((y(i)-offset)*scale)+height, startColor, endColor);
    }



    private void worldAnim(PoseStack poseStack, int pI, ForgeIngameGui gui, ClientPlayerData id, float partialTicks) {
        int currTick = 0;
        float scale = 3f;
        float scaleSlow = .75f;
        float translateX = (head.x - x)/head.scale;
        float translateY = (head.y - y)/head.scale;
        float alphaOld = 1f;
        float alphaNew = 0f;
        boolean renderText = false;
        if (id.dim.animTime > 90) {
            currTick = 10 - (id.dim.animTime - 90); //0-10
            scale = 3f*animPos(currTick, partialTicks, true, 10, 1);
            scaleSlow = .75f*animPos(currTick, partialTicks, true, 10, 1.5f);
            translateX = (head.x - x)/head.scale*(currTick+partialTicks)/10;
            translateY = (head.y - y)/head.scale*(currTick+partialTicks)/10;

        } else if (id.dim.animTime > 10) {
            currTick = id.dim.animTime - 10; //80 - 0
            renderText = true;
            alphaOld = animPos(currTick, partialTicks, false, 80, 1);
            alphaNew = 1f - alphaOld;
        } else { //10 - 0
            scale = 3f*animPos(id.dim.animTime, partialTicks, false, 10, 1);
            scaleSlow = .75f*animPos(id.dim.animTime, partialTicks, false, 10, 1.5f);
            translateX = (head.x - x)*(id.dim.animTime-partialTicks)/10;
            translateY = (head.y - y)*(id.dim.animTime-partialTicks)/10;
            alphaOld = 0f;
            alphaNew = 1f;
        }

        poseStack.pushPose();
        poseStack.scale(.25f+scaleSlow, .25f+scaleSlow, 1f);
        poseStack.translate(translateX, translateY, 10);
        int color, x, y;
        TextureAtlasSprite sprite;
        x = (int) (x(pI)*(4-scale)/head.scale);
        y = (int) (y(pI)*(4-scale)/head.scale);
        if (alphaOld > 0f) {

            sprite = DimConfig.sprite(id.dim.oldDimension);
            color = DimConfig.color(id.dim.oldDimension);
            setColor(1f,1f,1f,alphaOld);
            rectScaled(pI, poseStack, 0, (int) (-head.scale), ((color & 0xfefefe) >> 1) | (int)(255*alphaOld) << 24, color | (int)(255*alphaOld) << 24 , (4 - scale)/head.scale);

            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            RenderSystem.enableBlend();
            Gui.blit(poseStack, x, y, 0, 32, 32, sprite);

        }

        color = DimConfig.color(id.dim.dimension);
        if (alphaNew > 0f) {
            sprite = DimConfig.sprite(id.dim.dimension);
            setColor(1f,1f,1f,alphaNew);
            rectScaled(pI, poseStack, 0, (int) (-head.scale), ((color & 0xfefefe) >> 1) | (int)(255*alphaNew) << 24, color | (int)(255*alphaNew) << 24 , (4 - scale)/head.scale);


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
        if (!renderText) return;
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
            alphaPercent = (currTick - partialTicks)/5f;
            transX = -10-(10-(currTick-partialTicks))*4f;
        } else {
            transX = -10-(10-(currTick-partialTicks))*4f;
        }

        for (int j = 0; j < dim.dimName.size(); j++) {
            poseStack.pushPose();
            if (j % 2 == 1)
                poseStack.translate(-transX, 0, 10);
            else
                poseStack.translate(transX, 0, 10);
            x = (int) (head.x(partyIndex)-gui.getFont().width(dim.dimName.get(j))/2f)+16;
            //TODO: Remove hardcoding of 11 for when scaling is allowed.
            y = head.y(partyIndex) + 16 + (j*gui.getFont().lineHeight) - ((gui.getFont().lineHeight*dim.dimName.size()-1)>>1);
           if (alphaPercent > 0f) {
                gui.getFont().drawShadow(poseStack, dim.dimName.get(j), x, y, color | ((int)(255*alphaPercent) << 24));
            }
            poseStack.popPose();
        }
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h);
        c.addTitleEntry("config.sedparties.title.display");
        c.addBooleanEntry("config.sedparties.name.display", isEnabled());
        c.addBooleanEntry("config.sedparties.name.tdisplay", renderText);
        c.addBooleanEntry("config.sedparties.name.danim", DimAnim.animActive);
        c.addTitleEntry("config.sedparties.title.position");
        c.addSliderEntry("config.sedparties.name.xpos", 0, () -> Math.max(clickArea.r(0), frameX + frameW) - frameW + 32, this.x);
        c.addSliderEntry("config.sedparties.name.ypos", 0, () -> Math.max(clickArea.b(0), frameY + frameH) - frameY + 32, this.y);
        c.addSliderEntry("config.sedparties.name.zpos", 0, () -> 10, zPos);


        return c;
    }

    @Override
    protected void itemStart(PoseStack poseStack) {
        poseStack.pushPose();
        if (head != null)
            poseStack.scale(head.scale, head.scale, 1);
        poseStack.translate(0,0, zPos);
    }
    @Override
    protected void tooltipStart(PoseStack poseStack) {
        poseStack.scale(1/head.scale, 1/head.scale, 1);
    }
    @Override
    public void toggleText(boolean data) {
        renderText = data;
    }


    @Override
    protected void updateValues() {
    }

}
