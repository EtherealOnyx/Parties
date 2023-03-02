package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.anim.DimAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;
import static io.sedu.mc.parties.util.AnimUtils.animPos;

public class PDimIcon extends RenderSelfItem {

    public static ItemStack icon = null;
    protected static PHead head = null;

    public PDimIcon(String name) {
        super(name);
        width = 8;
        height = 8;
        scale = 1f;
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

            rectScaled(pI, poseStack, zPos, -head.scale, ((color & 0xfefefe) >> 1) | id.alphaI << 24, color | id.alphaI << 24, 1/head.scale);

            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            RenderSystem.enableBlend();
            Gui.blit(poseStack, (int) ((x(pI))/head.scale), (int) ((y(pI))/head.scale), zPos, width, height, sprite);

            //Tooltip Render
            if (notEditing() && withinBounds(xNormal(pI), yNormal(pI), width, height, 4, scale)) {
                renderTooltip(poseStack, gui, 10, 0, id.dim.dimNorm, (color & 0xfefefe) >> 1, color, 0, (color & 0xfefefe) >> 1, color);
            }
        });

    }

    void rectScaled(int i, PoseStack pose, int z, float offset, int width, int height, int startColor, int endColor, float scale) {
        RenderUtils.rect(pose.last().pose(), z, ((x(i)+offset)*scale), ((y(i)+offset)*scale), ((x(i)-offset)*scale)+width, ((y(i)-offset)*scale)+height, startColor, endColor);
    }

    void rectScaled(int i, PoseStack pose, int z, float offset, int startColor, int endColor, float scale) {
        RenderUtils.rect(pose.last().pose(), z, ((x(i)+offset)*scale), ((y(i)+offset)*scale), ((x(i)-offset)*scale)+width, ((y(i)-offset)*scale)+height, startColor, endColor);
    }



    private void worldAnim(PoseStack poseStack, int pI, ForgeIngameGui gui, ClientPlayerData id, float partialTicks) {
        int currTick = 0;
        float translateX = (head.x - x)/head.scale;
        float translateY = (head.y - y)/head.scale;
        int size;
        float alphaOld = 1f;
        float alphaNew = 0f;
        boolean renderText = false;
        float offY = 0;
        if (id.dim.animTime > 90) {
            currTick = 10 - (id.dim.animTime - 90); //0-10
            float curPos = animPos(currTick, partialTicks, true, 10, 2);
            translateX = (head.x - x)/head.scale*curPos;
            translateY = (head.y - y)/head.scale*curPos;
            size = (int) (8 + (24*curPos));
            offY = (currTick + partialTicks - 5);
            offY *= -offY;
            offY += 25;
        } else if (id.dim.animTime > 10) {
            currTick = id.dim.animTime - 10; //80 - 0
            renderText = true;
            alphaOld = animPos(currTick, partialTicks, false, 80, 1);
            alphaNew = 1f - alphaOld;
            size = 32;
        } else { //10 - 0
            float curPos = animPos(id.dim.animTime, partialTicks, false, 10, 2);
            translateX = (head.x - x)/head.scale*curPos;
            translateY = (head.y - y)/head.scale*curPos;
            alphaOld = 0f;
            alphaNew = 1f;
            size = (int) (8 + (24*curPos));
        }
        translateY += offY;

        poseStack.pushPose();
        poseStack.translate(translateX, translateY, 0);
        int color, x, y;
        TextureAtlasSprite sprite;
        x = (int) (x(pI)/head.scale);
        y = (int) (y(pI)/head.scale);
        if (alphaOld > 0f) {

            sprite = DimConfig.sprite(id.dim.oldDimension);
            color = DimConfig.color(id.dim.oldDimension);
            setColor(1f,1f,1f,alphaOld);
            rectScaled(pI, poseStack, zPos, -head.scale, size,size, ((color & 0xfefefe) >> 1) | (int)(255*alphaOld) << 24, color | (int)(255*alphaOld) << 24 , 1/head.scale);

            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            RenderSystem.enableBlend();
            Gui.blit(poseStack, x, y, zPos, size,size, sprite);

        }

        color = DimConfig.color(id.dim.dimension);
        if (alphaNew > 0f) {
            sprite = DimConfig.sprite(id.dim.dimension);
            setColor(1f,1f,1f,alphaNew);
            rectScaled(pI, poseStack, zPos, -head.scale, size,size, ((color & 0xfefefe) >> 1) | (int)(255*alphaNew) << 24, color | (int)(255*alphaNew) << 24 , 1/head.scale);


            RenderSystem.setShaderTexture(0, sprite.atlas().location());
            RenderSystem.enableBlend();
            Gui.blit(poseStack, x, y, zPos, size,size, sprite);

        }
        poseStack.popPose();

        if (renderText)
            //TODO: Revert id.dim to id.dim.dimName and extract sounds to their own implementation
            doTextRender(pI, gui, poseStack, currTick, partialTicks, id.dim, color);
        resetColor();
    }



    private void doTextRender(int partyIndex, ForgeIngameGui gui, PoseStack poseStack, int currTick, float partialTicks, DimAnim dim, int color) {
        if (!textEnabled) return;
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

        RenderSystem.enableDepthTest();
        for (int j = 0; j < dim.dimName.size(); j++) {
            poseStack.pushPose();
            if (j % 2 == 1)
                poseStack.translate(-transX, 0, zPos+10);
            else
                poseStack.translate(transX, 0, zPos+10);
            x = (int) (head.x(partyIndex)-gui.getFont().width(dim.dimName.get(j))/2f)+16;
            y = head.y(partyIndex) + 16 + (j*gui.getFont().lineHeight) - ((gui.getFont().lineHeight*dim.dimName.size()-1)>>1);
           if (alphaPercent > 0f) {
                gui.getFont().drawShadow(poseStack, dim.dimName.get(j), x, y, color | ((int)(255*alphaPercent) << 24));
            }
            poseStack.popPose();
        }
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("display");
        c.addBooleanEntry("display", isEnabled());
        c.addBooleanEntry("tdisplay", textEnabled);
        c.addBooleanEntry("danim", DimAnim.animActive);
        c.addTitleEntry("position");
        c.addSliderEntry("xpos", 0, this::maxX, this.x);
        c.addSliderEntry("ypos", 0, this::maxY, this.y);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);


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

    protected int maxX() {
        return Math.max(0, frameEleW - (int)(width*head.scale));
    }

    protected int maxY() {
        return Math.max(0, frameEleH - (int)(height*head.scale));
    }

    @Override
    protected void updateValues() {
        //TODO: Add to here.
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true);
        e.addEntry("tdisplay", true);
        e.addEntry("danim", true);
        e.addEntry("xpos", 5);
        e.addEntry("ypos", 34);
        e.addEntry("zpos", 1);
        return e;
    }

}
