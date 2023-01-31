package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PRectO extends RenderSelfItem {


    public PRectO(String name, int x, int y, int w, int h) {
        super(name, x, y, w, h);

    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            rect(i, poseStack,-5, 0, 0x44002024, 0x44002024);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        rect(i, poseStack,-5, 0, 0x44002024, 0x44002024);
    }
}