package io.sedu.mc.parties.client.overlay;

import Util.Render;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PHealthText extends RenderSelfItem {

    int color;
    int absorbColor;
    int deadColor;

    public PHealthText(String name, int x, int y, int color, int absorbColor, int deadColor) {
        super(name, x, y);
        this.color = color;
        this.absorbColor = absorbColor;
        this.deadColor = deadColor;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    int getColor() {
        return 0xe3403d;
    }

    @Override
    public String getType() {
        return "Text";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Render.sizeRectNoA(poseStack.last().pose(), b.x+7, b.y+9, 22, 7, 0x111111, 0x111111);
        Render.sizeRectNoA(poseStack.last().pose(), b.x+8, b.y+10, 20, 5, 0xC52C27, 0x6C0D15);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack,b.x+3, b.y+8, 16, 0, 9, 9);
        blit(poseStack,b.x+3, b.y+8, 52, 0, 9, 9);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (!id.isDead) {
            if (id.health.absorb > 0) {
                textCentered(i, gui, poseStack, (int)Math.ceil(id.health.cur+id.health.absorb) + "/" + (int)id.health.max, absorbColor);
            } else {
                textCentered(i, gui, poseStack, (int)Math.ceil(id.health.cur) + "/" + (int)id.health.max, color);
            }
        } else {
            textCentered(i, gui, poseStack, "Dead", deadColor);
        }
    }


}
