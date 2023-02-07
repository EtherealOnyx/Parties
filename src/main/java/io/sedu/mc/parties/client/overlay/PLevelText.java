package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.*;

public class PLevelText extends RenderSelfItem {
    int color;

    public PLevelText(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderText(gui, poseStack, String.valueOf(id.getXpLevel()), x(i) - String.valueOf(id.getXpLevel()).length()*3, y(i), id.getXpBar());
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        renderText(gui, poseStack, String.valueOf(id.getLevelForced()), x(i) - (gui.getFont().width(String.valueOf(id.getLevelForced()))>>1), y(i), id.getXpBarForced());

    }

    private void renderText(ForgeIngameGui g, PoseStack poseStack, String s, int x, int y, float level) {
        g.getFont().draw(poseStack, s, (float)(x + 1), (float)y, 0);
        g.getFont().draw(poseStack, s, (float)(x - 1), (float)y, 0);
        g.getFont().draw(poseStack, s, (float)x, (float)(y + 1), 0);
        g.getFont().draw(poseStack, s, (float)x, (float)(y - 1), 0);
        g.getFont().draw(poseStack, s, (float)x, (float)y, 8453920); //8453920

        if (isActive() && withinBounds(x, y, x+g.getFont().width(s), y + g.getFont().lineHeight, 2)) {
            renderXpTooltip(poseStack, g, 10, 0, level);
        }

    }

    protected void renderXpTooltip(PoseStack poseStack, ForgeIngameGui gui, int offsetX, int offsetY, float level) {

        poseStack.pushPose();
        poseStack.translate(0, 0, 400);
        rectCO(poseStack, 0, -3, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+offsetX+182, currentY+mouseY()+5+offsetY, 0x8ec265, 0x385e1a);
        rectCO(poseStack, 0, -2, mouseX()+offsetX, currentY+mouseY()+offsetY, mouseX()+offsetX+182, currentY+mouseY()+5+offsetY, 0x140514, 0x140514);
        setup(Gui.GUI_ICONS_LOCATION);
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        blit(poseStack, mouseX()+offsetX, currentY+mouseY()+offsetY, 0, 64, 182, 5);
        blit(poseStack, mouseX()+offsetX, currentY+mouseY()+offsetY, 0, 69, (int) (182*level), 5);
        //gui.getFont().draw(poseStack, text, mouseX()+offsetX, currentY+mouseY()+1, textColor);
        //gui.getFont().drawShadow(poseStack, text, mouseX()+offsetX, currentY+mouseY()+1, textColor);
        poseStack.popPose();

        currentY += gui.getFont().lineHeight+offsetY+8;

    }
}