package io.sedu.mc.parties.client.overlay;

import Util.Render;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PRectD extends RenderItem {


    public PRectD(String n, int x, int y, int w, int h) {
        super(n, x, y, w, h);
    }

    @Override
    int getColor() {
        return 0xDDFFFF;
    }

    @Override
    public String getType() {
        return "BG";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        Render.sizeRectNoA(poseStack.last().pose(), b.x+9, b.y+5, 14, 14, 0xd3ffff, 0x779fa9);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        rect(i, poseStack,-2, 0, 0x44002024, 0x44002024);
    }
}
