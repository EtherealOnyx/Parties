package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PAbsorb extends RenderSelfItem {

    public PAbsorb(String name, int x, int y) {
        super(name, x, y);

    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        useAlpha(id.alpha);
        if (id.getHealth() + id.getAbsorb() > id.getMaxHealth() && !id.isDead) {
            setup(Gui.GUI_ICONS_LOCATION);
            gui.blit(poseStack,x(i), y(i)-1, 16, 0, 9, 9);
            gui.blit(poseStack,x(i), y(i)-1, 160, 0, 9, 9);
            resetColor();
        }

    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack,partialTicks);
    }
}
