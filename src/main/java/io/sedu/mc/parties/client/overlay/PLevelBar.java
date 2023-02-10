package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PLevelBar extends RenderSelfItem {


    public PLevelBar(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }

    @Override
    int getColor() {
        return 0x7efc20;
    }

    @Override
    public String getType() {
        return "Bar";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        assert Minecraft.getInstance().player != null;
        float bar = Minecraft.getInstance().player.experienceProgress;
        setup(Gui.GUI_ICONS_LOCATION);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack, b.x+2, b.y+10, 0, 64, 14, 5);
        blit(poseStack, b.x+16, b.y+10, 168, 64, 14, 5);
        int w = (int) (28*bar);
        if (w > 14) {
            blit(poseStack, b.x+2, b.y+10, 0, 69, 14, 5);
            blit(poseStack, b.x+16, b.y+10, 168, 69, w-14, 5);
        } else {
            blit(poseStack, b.x+2,b.y+10, 0, 69, w, 5);
        }
        if (w > 14) {
            blit(poseStack, b.x+2, b.y+10, 0, 69, 14, 5);
            blit(poseStack, b.x+16, b.y+10, 168, 69, w-14, 5);
        } else {
            blit(poseStack, b.x+2,b.y+10, 0, 69, w, 5);
        }
        String level = String.valueOf(Minecraft.getInstance().player.experienceLevel);
        int x = b.x + 16 - (gui.getFont().width(level)>>1);
        int y = b.y + 9;
        gui.getFont().draw(poseStack, level, (float)(x + 1), y, 0);
        gui.getFont().draw(poseStack, level, (float)(x - 1), (float)y, 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)(y + 1), 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)(y - 1), 0);
        gui.getFont().draw(poseStack, level, (float)x, (float)y, 8453920);

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
        RenderSystem.enableDepthTest();
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
