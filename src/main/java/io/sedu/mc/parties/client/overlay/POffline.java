package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class POffline extends RenderSelfItem {

    public POffline(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        x = 154;
        if (!id.isOnline) {
            setup(Gui.GUI_ICONS_LOCATION);
            blit(poseStack, x(i), y(i), 0, 216, 10, 8);
        }
    }
}
