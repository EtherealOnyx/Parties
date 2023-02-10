package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
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
    int getColor() {
        return 0x7efc20;
    }

    @Override
    public String getType() {
        return "Text";
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
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        renderText(gui, poseStack, String.valueOf(id.getLevelForced()), x(i) - (gui.getFont().width(String.valueOf(id.getLevelForced()))>>1), y(i), id.getXpBarForced());

    }

    private void renderText(ForgeIngameGui g, PoseStack poseStack, String s, int x, int y, float level) {
        poseStack.translate(0,0,1);
        g.getFont().draw(poseStack, s, (float)(x + 1), (float)y, 0);
        g.getFont().draw(poseStack, s, (float)(x - 1), (float)y, 0);
        g.getFont().draw(poseStack, s, (float)x, (float)(y + 1), 0);
        g.getFont().draw(poseStack, s, (float)x, (float)(y - 1), 0);
        g.getFont().draw(poseStack, s, (float)x, (float)y, 8453920);
        poseStack.translate(0,0,-1);

        if (notEditing() && withinBounds(x, y, x+g.getFont().width(s), y + g.getFont().lineHeight, 2)) {
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