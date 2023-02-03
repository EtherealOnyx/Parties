package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PDead extends RenderItem {

    public PDead(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isDead) {
            setup(Gui.GUI_ICONS_LOCATION);
            blit(poseStack,x(i), y(i), 16 + (gui.getGuiTicks() >> 4 & 1)*9, 0, 9, 9);


        }
    }
}
