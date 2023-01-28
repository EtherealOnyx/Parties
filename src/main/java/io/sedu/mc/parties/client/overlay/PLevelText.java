package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PLevelText extends RenderSelfItem {
    int color;

    public PLevelText(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            text(i, gui, poseStack, String.valueOf(id.getXpLevel()), color);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        text(i, gui, poseStack, String.valueOf(id.getLevelForced()), color);
    }
}