package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.config.ConfigEntry;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.util.RenderUtils.renderEntityInInventoryAttempt2;

public class PHead extends RenderSelfItem implements TooltipItem {

    public static ItemStack icon = null;
    protected static int renderType = 0; //0 = no body render, 1 = render self only, 2 = render player.
    private Renderer renderSelf;
    private Renderer renderMember;
    protected static boolean renderBleed = false;

    public PHead(String name) {
        super(name);
        PDimIcon.head = this;
        width = 32;
        height = 32;
        renderSelf = (i, id, gui, poseStack, partialTicks) -> {
            if (renderType != 0 && id.clientPlayer != null && !id.getDim().active)  {
                RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 0x33FFFFFF);
                renderEntityInInventoryAttempt2((int) ((x(i)+16)*scale), (int) (y(i)*scale), scale, (int) (15*scale), id.clientPlayer, partialTicks);

                return;
            }
            RenderSystem.enableDepthTest();
            if (id.isDead)
                setColor(1f, .5f, .5f, .5f);
            else
                setColor(1f, 1f, 1f, id.alpha);
            RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, id.alpha, 0.5f, id.alpha);
            setup(id.getHead());
            blit(poseStack, x(i), y(i), 32, 32, 32, 32); blit(poseStack, x(i), y(i), 160, 32, 32, 32);
            resetColor();
        };
        renderMember = (i, id, gui, poseStack, partialTicks) -> {
            if (renderType == 2 && id.shouldRenderModel)  {
                RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 0x33FFFFFF);
                RenderItem.setColor(0,0,0,0);
                renderEntityInInventoryAttempt2((int) ((x(i)+16)*scale), (int) (y(i)*scale), scale, (int) (15*scale), id.clientPlayer, partialTicks);
                RenderItem.resetColor();
                return;
            }
            RenderSystem.enableDepthTest();
            if (id.isDead)
                setColor(1f, .5f, .5f, .5f);
            else
                setColor(1f, 1f, 1f, id.alpha);
            RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, id.alpha, 0.5f, id.alpha);
            setup(id.getHead());
            blit(poseStack, x(i), y(i), 32, 32, 32, 32); blit(poseStack, x(i), y(i), 160, 32, 32, 32);
            resetColor();
        };
    }

    @Override
    int getColor() {
        return 0xAACCAA;
    }

    public void updateRendererForMods() {
        renderSelf = (i, id, gui, poseStack, partialTicks) -> {
            if (id.getBleeding()) {
                resetColor();
                RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, 1f, 0.5f, 1f);
                setup(id.getHead());
                blit(poseStack, x(i), y(i), 32, 32, 32, 32); blit(poseStack, x(i), y(i), 160, 32, 32, 32);
                float heightProgress = height*(1 - id.getReviveProgress());
                float alpha = (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 8f) / 10f);

                RenderUtils.grayRectForHead(poseStack.last().pose(), x(i), y(i), zPos, -1, width, heightProgress, .10f, alpha);
                useAlpha((float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 2f) / 4f));
                RenderUtils.horizRect(poseStack.last().pose(), zPos, x(i)-4, y(i)+heightProgress, x(i) + (width>>1), y(i)+heightProgress+1, 0xFFFFFF, 0xFFFFFFFF);
                RenderUtils.horizRect(poseStack.last().pose(), zPos, x(i) + (width>>1), y(i)+heightProgress, x(i)+width+4, y(i)+heightProgress + 1, 0xFFFFFFFF, 0xFFFFFF);

                if (!renderBleed) return;
                poseStack.pushPose();
                poseStack.scale(.5f,.5f,1f);
                resetColor();
                text((x(i)+4)*2, (y(i))*2, gui, poseStack, "§lBleeding", 0xa15252);
                poseStack.popPose();
                return;
            }
            if (id.getDowned()) {
                resetColor();
                RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, 1f, 0.5f, 1f);
                setup(id.getHead());
                blit(poseStack, x(i), y(i), 32, 32, 32, 32); blit(poseStack, x(i), y(i), 160, 32, 32, 32);
                float heightProgress = height*(1 - id.getReviveProgress());
                float alpha = (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 8f) / 10f);

                RenderUtils.grayRectForHead(poseStack.last().pose(), x(i), y(i), zPos, -1, width, heightProgress, .10f, alpha);
                useAlpha((float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 2f) / 4f));
                RenderUtils.horizRect(poseStack.last().pose(), zPos, x(i)-4, y(i)+heightProgress, x(i) + (width>>1), y(i)+heightProgress+1, 0xFFFFFF, 0xFFFFFFFF);
                RenderUtils.horizRect(poseStack.last().pose(), zPos, x(i) + (width>>1), y(i)+heightProgress, x(i)+width+4, y(i)+heightProgress + 1, 0xFFFFFFFF, 0xFFFFFF);

                if (!renderBleed) return;
                poseStack.pushPose();
                poseStack.scale(.5f,.5f,1f);
                resetColor();
                text((x(i)+10)*2, (y(i))*2, gui, poseStack, "§lK.O'd", 0xa15252);
                poseStack.popPose();
                return;
            }
            if (renderType != 0 && id.clientPlayer != null && !id.getDim().active)  {
                RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 0x33FFFFFF);
                renderEntityInInventoryAttempt2((int) ((x(i)+16)*scale), (int) ((y(i))*scale), scale, (int) (15*scale), id.clientPlayer, partialTicks);
                return;
            }
            RenderSystem.enableDepthTest();
            if (id.isDead)
                setColor(1f, .5f, .5f, .5f);
            else
                setColor(1f, 1f, 1f, id.alpha);
            RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, id.alpha, 0.5f, id.alpha);
            setup(id.getHead());
            blit(poseStack, x(i), y(i), 32, 32, 32, 32); blit(poseStack, x(i), y(i), 160, 32, 32, 32);
            resetColor();
        };
        renderMember = (i, id, gui, poseStack, partialTicks) -> {
            if (id.getBleeding()) {
                resetColor();
                RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, 1f, 0.5f, 1f);
                setup(id.getHead());
                blit(poseStack, x(i), y(i), 32, 32, 32, 32); blit(poseStack, x(i), y(i), 160, 32, 32, 32);
                float heightProgress = height*(1 - id.getReviveProgress());
                float alpha = (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 8f) / 10f);

                RenderUtils.grayRectForHead(poseStack.last().pose(), x(i), y(i), zPos, -1, width, heightProgress, .10f, alpha);
                useAlpha((float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 2f) / 4f));
                RenderUtils.horizRect(poseStack.last().pose(), zPos, x(i)-4, y(i)+heightProgress, x(i) + (width>>1), y(i)+heightProgress+1, 0xFFFFFF, 0xFFFFFFFF);
                RenderUtils.horizRect(poseStack.last().pose(), zPos, x(i) + (width>>1), y(i)+heightProgress, x(i)+width+4, y(i)+heightProgress + 1, 0xFFFFFFFF, 0xFFFFFF);

                if (!renderBleed) return;
                poseStack.pushPose();
                poseStack.scale(.5f,.5f,1f);
                useAlpha(alpha);
                text((x(i)+4)*2, (y(i))*2, gui, poseStack, "§lBleeding", 0xa15252);
                poseStack.popPose();
                resetColor();
                return;
            }
            if (id.getDowned()) {
                resetColor();
                RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, 1f, 0.5f, 1f);
                setup(id.getHead());
                blit(poseStack, x(i), y(i), 32, 32, 32, 32); blit(poseStack, x(i), y(i), 160, 32, 32, 32);
                float heightProgress = height*(1 - id.getReviveProgress());
                float alpha = (float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 8f) / 10f);

                RenderUtils.grayRectForHead(poseStack.last().pose(), x(i), y(i), zPos, -1, width, heightProgress, .10f, alpha);
                useAlpha((float) (.75f + Math.sin((gui.getGuiTicks() + partialTicks) / 2f) / 4f));
                RenderUtils.horizRect(poseStack.last().pose(), zPos, x(i)-4, y(i)+heightProgress, x(i) + (width>>1), y(i)+heightProgress+1, 0xFFFFFF, 0xFFFFFFFF);
                RenderUtils.horizRect(poseStack.last().pose(), zPos, x(i) + (width>>1), y(i)+heightProgress, x(i)+width+4, y(i)+heightProgress + 1, 0xFFFFFFFF, 0xFFFFFF);

                if (!renderBleed) return;
                poseStack.pushPose();
                poseStack.scale(.5f,.5f,1f);
                resetColor();
                text((x(i)+10)*2, (y(i))*2, gui, poseStack, "§lK.O'd", 0xa15252);
                poseStack.popPose();
                return;
            }
            if (renderType == 2 && id.shouldRenderModel)  {
                RenderUtils.sizeRect(poseStack.last().pose(), x(i), y(i), zPos, width, height, 0x33FFFFFF);
                RenderItem.setColor(0,0,0,0);
                renderEntityInInventoryAttempt2((int) ((x(i)+16)*scale), (int) (y(i)*scale), scale, (int) (15*scale), id.clientPlayer, partialTicks);
                RenderItem.resetColor();
                return;
            }
            RenderSystem.enableDepthTest();
            if (id.isDead)
                setColor(1f, .5f, .5f, .5f);
            else
                setColor(1f, 1f, 1f, id.alpha);
            RenderUtils.grayRect(poseStack.last().pose(), x(i), y(i), zPos, -1, width, height, .05f, id.alpha, 0.5f, id.alpha);
            setup(id.getHead());
            blit(poseStack, x(i), y(i), 32, 32, 32, 32); blit(poseStack, x(i), y(i), 160, 32, 32, 32);
            resetColor();
        };
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, b.x+4, b.y, 0);
    }

    @Override
    public ConfigEntry getDefaults() {
        ConfigEntry e = new ConfigEntry();
        e.addEntry("display", true, 1);
        e.addEntry("htype", 1, 2);
        e.addEntry("xpos", 8, 12);
        e.addEntry("ypos", 8, 12);
        e.addEntry("zpos", 0, 4);
        e.addEntry("scale", 2, 2);
        e.addEntry("bleed", true, 1);
        return e;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        this.renderMember.render(i,id,gui,poseStack,partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        this.renderSelf.render(i,id,gui,poseStack,partialTicks);

    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h, boolean parse) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h, parse);
        c.addTitleEntry("display");
        c.addBooleanEntry("display", isEnabled());
        c.addSliderEntry("htype", 0, () -> 2, renderType);
        c.addSliderEntry("xpos", 0, this::maxX, this.x);
        c.addSliderEntry("ypos", 0, this::maxY, this.y);
        c.addSliderEntry("zpos", 0, () -> 10, zPos);
        c.addSliderEntry("scale", 1, () -> 3, getScale(), true);
        c.addBooleanEntry("bleed", renderBleed);
        return c;
    }


    @Override
    public void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY) {
        ClientPlayerData.getOrderedPlayer(index, p -> renderTooltip(poseStack, gui, mouseX, mouseY, 10, 0, p.getName(), 0xDDDDDD, 0xAAAAAA, 0xFFFFFF));
    }

    private interface Renderer {
        void render(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);
    }
}
