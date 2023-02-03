package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PHead extends RenderItem {


    public PHead(String name, int x, int y) {
        super(name, x, y, 32, 32);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isDead)
            setColor(1f, .5f, .5f, .5f);
        else
            setColor(1f, 1f, 1f, id.alpha);
        rect(i, poseStack,0,  -1,  0x111111, 0x555555, (int)(id.alphaI*.75));
        setup(id.getHead());
        blit(poseStack, x(i), y(i), 32, 32, 32, 32);
        resetColor();
    }
}
