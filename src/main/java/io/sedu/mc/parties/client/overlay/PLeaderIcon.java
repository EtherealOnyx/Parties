package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PLeaderIcon extends RenderItem {

    public PLeaderIcon(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isLeader()) {
            useAlpha(id.alpha);
            setup(partyPath);
            gui.blit(poseStack, x(i), y(i), 0, 0, 9, 9);
            resetColor();
        }

    }
}
