package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PLevelBar extends RenderSelfItem {


    public PLevelBar(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline) {
            renderBar(i, poseStack, id.getXpBar());
        }
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        //useAlpha(id.alpha);
        renderBar(i, poseStack, id.getXpBarForced());
    }

    void renderBar(int i, PoseStack poseStack, float bar) {
        setup(Gui.GUI_ICONS_LOCATION);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        //this.blit(poseStack, pXPos, l, 0, 64, 182, 5);
        blit(poseStack, x(i), y(i), 0, 64, width>>1, height);
        blit(poseStack, x(i)+(width>>1), y(i), 182-(width>>1), 64, width>>1, height);
        int w = (int) (width*bar);

        if (w > width>>1) {
            blit(poseStack, x(i), y(i), 0, 69, width>>1, height);
            blit(poseStack, x(i)+(width>>1), y(i), 182-(width>>1), 69, w-(width>>1), height);
        } else {
            blit(poseStack, x(i), y(i), 0, 69, w, height);
        }
    }




}
