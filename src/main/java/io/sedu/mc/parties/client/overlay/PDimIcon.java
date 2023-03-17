package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.config.DimConfig;
import io.sedu.mc.parties.client.overlay.anim.DimAnim;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.util.AnimUtils.animPos;

public class PDimIcon extends RenderSelfItem implements TooltipItem {

    protected static PHead head = null;

    public PDimIcon(String name) {
        super(name);
        width = 10;
        height = 10;
    }

    @Override
    boolean isInBound(int mouseX, int mouseY) {
        return mouseX > x - 2 && mouseY > y - 2
                && mouseX < x + 2 + width*head.scale && mouseY < y + 2 + height*head.scale;
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
        DimConfig.entry("minecraft:overworld", (icon, color) -> renderGuiItem(icon, b.x+7, b.y+3, .75f, 5));
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
        DimConfig.entry(id.dim.dimension, (icon, color) -> {
            renderGuiItem(icon, x(pI), y(pI), .75f*head.scale, 5*head.scale);

        });

    }

    protected void renderGuiItem(ItemStack iStack, int pX, int pY, float scale, float scalePos) {
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(iStack, (Level)null, Minecraft.getInstance().player, 0);
        Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(pX, pY, zPos+2);
        posestack.translate(scalePos, scalePos, 0.0D);
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(16.0F, 16.0F, 1F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.scale(scale,scale,1f);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.setupGuiFlatDiffuseLighting(RenderUtils.POS, RenderUtils.NEG);

        Minecraft.getInstance().getItemRenderer().render(iStack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    protected void renderGuiItem(ItemStack iStack, int pX, int pY, float x, float y, float scale, float scalePos) {
        BakedModel bakedmodel = Minecraft.getInstance().getItemRenderer().getModel(iStack, (Level)null, Minecraft.getInstance().player, 0);
        Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(pX, pY, zPos+2);
        posestack.translate(scalePos, scalePos, 0.0D);
        posestack.translate(x, y, 0);
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(16.0F, 16.0F, 1F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.scale(scale*head.scale,scale*head.scale,1f);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.setupGuiFlatDiffuseLighting(RenderUtils.POS, RenderUtils.NEG);

        Minecraft.getInstance().getItemRenderer().render(iStack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private void worldAnim(PoseStack poseStack, int pI, ForgeIngameGui gui, ClientPlayerData id, float partialTicks) {
        int currTick = 0;
        float translateX = (head.x - x);
        float translateY = (head.y - y);
        float scale = 2f;
        float scalePos = 16*head.scale;
        boolean renderText = false;
        float offY = 0;
        if (id.dim.animTime > 90) {
            currTick = 10 - (id.dim.animTime - 90); //0-10
            float curPos = animPos(currTick, partialTicks, true, 10, 2);
            translateX = (head.x - x)*curPos;
            translateY = (head.y - y)*curPos;
            offY = (currTick + partialTicks - 5);
            offY *= -offY;
            offY += 25;
            scale = .75f + (1.25f)*curPos;
            scalePos = (5F + 11F*curPos)*head.scale;
        } else if (id.dim.animTime > 10) {
            currTick = id.dim.animTime - 10; //80 - 0
            renderText = true;
        } else { //10 - 0
            float curPos = animPos(id.dim.animTime, partialTicks, false, 10, 2);
            translateX = (head.x - x)*curPos;
            translateY = (head.y - y)*curPos;
            scale = .75f + (1.25f)*curPos;
            scalePos = (5F + 11F*curPos)*head.scale;
        }
        translateY += offY;

        renderGuiItem(DimConfig.item(id.dim.dimension), x(pI), y(pI), translateX, translateY, scale, scalePos);

        if (renderText)
            //TODO: Extract sounds to their own implementation
            doTextRender(pI, gui, poseStack, currTick, partialTicks, id.dim, DimConfig.color(id.dim.dimension));
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

    protected int maxX() {
        return Math.max(0, frameEleW - (int)(width*head.scale));
    }

    protected int maxY() {
        return Math.max(0, frameEleH - (int)(height*head.scale));
    }

    @Override
    protected void updateValues() {
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("tdisplay", true, 1);
        e.addEntry("danim", true, 1);
        e.addEntry("xpos", 4, 12);
        e.addEntry("ypos", 32, 12);
        e.addEntry("zpos", 1, 4);
        return e;
    }

    public ItemBound getRenderItemBound() {
        return new ItemBound(frameX + x, frameY + y, (int) (width*head.scale), (int) (height*head.scale));
    }

    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData p;
        if ((p = ClientPlayerData.getOrderedPlayer(index)).isOnline) {
            int color = DimConfig.color(p.dim.dimension);
            int darkCol = (color & 0xfefefe) >> 1;
            renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, p.dim.dimNorm, darkCol, color, 0, darkCol, color);
        }

    }

    @Override
    public SmallBound changeVisibility(boolean data) {
        DimAnim.animActive = data;
        return super.changeVisibility(data);
    }
}
