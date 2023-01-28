package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PRectD extends RenderItem {


    public PRectD(String n, int x, int y, int w, int h) {
        super(n, x, y, w, h);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        rect(i, poseStack,-2, 0, 0x44002024, 0x44002024);
    }
}
