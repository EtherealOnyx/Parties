package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class POfflineText extends RenderSelfItem {

    int color;

    public POfflineText(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (!id.isOnline) {
            setup(Gui.GUI_ICONS_LOCATION);
            gui.getFont().draw(poseStack, "§oOffline...", x(i), y(i), color);
            gui.getFont().drawShadow(poseStack, "§oOffline...", x(i), y(i), color);
        }
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {

    }
}
