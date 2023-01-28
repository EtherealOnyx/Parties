package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PName extends RenderItem {

    int color;

    public PName(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        textS(i, gui, poseStack, id.getName(), color);
    }


}
