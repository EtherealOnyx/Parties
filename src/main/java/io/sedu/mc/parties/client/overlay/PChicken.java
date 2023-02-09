package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;

public class PChicken extends RenderSelfItem {


    public PChicken(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        renderChicken(i, gui, poseStack, id.getHungerForced(), id.alpha);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderChicken(i, gui, poseStack, id.getHunger(), id.alpha);

    }

    void renderChicken(int i, ForgeIngameGui gui, PoseStack poseStack, int hunger, float alpha) {
        useAlpha(alpha);
        setup(Gui.GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();

        if (hunger > 16) {
            blit(poseStack, x(i), y(i), 16, 27, 9, 9);
            blit(poseStack, x(i), y(i), 52, 27, 9, 9);
        }
        else if (hunger > 12) {
            blit(poseStack, x(i), y(i), 16, 27, 9, 9);
            blit(poseStack, x(i), y(i), 61 - (gui.getGuiTicks() >> 4 & 1)*9, 27, 9, 9);
        } else if (hunger > 4) {
            blit(poseStack, x(i), y(i), 16, 27, 9, 9);
            if ((gui.getGuiTicks() >> 4 & 1) == 0)
                blit(poseStack, x(i), y(i), 61, 27, 9, 9);
        } else
            blit(poseStack, x(i), y(i), 16 + (gui.getGuiTicks() >> 3 & 1)*9, 27, 9, 9);


        resetColor();
        if (notEditing() && withinBounds(x(i), y(i), x(i)+9, y(i)+9, 2)) {
            renderTooltip(poseStack, gui, 10, 0, "Hunger: " + hunger + "/20", 0xb88458, 0x613c1b, 0xffd5b0);
        }
    }
}
