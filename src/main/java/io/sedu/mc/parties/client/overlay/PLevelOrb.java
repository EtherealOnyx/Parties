package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PLevelOrb extends RenderSelfItem {


    public PLevelOrb(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline) {
            useAlpha(id.alpha);
            setup(partyPath);
            blit(poseStack, x(i) - String.valueOf(id.getXpLevel()).length()*3, y(i), 9, 0, 9, 9);
            resetColor();
        }
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        useAlpha(id.alpha);
        setup(partyPath);
        blit(poseStack, x(i) - String.valueOf(id.getLevelForced()).length()*3, y(i), 9, 0, 9, 9);
        resetColor();
    }
}
