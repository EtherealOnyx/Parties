package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PLevelOrb extends RenderSelfItem {


    public PLevelOrb(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        setup(partyPath);
        gui.blit(poseStack, x(i), y(i), 9, 0, 9, 9);
    }
}
