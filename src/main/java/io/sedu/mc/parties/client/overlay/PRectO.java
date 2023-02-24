package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PRectO extends RenderSelfItem {


    public PRectO(String name) {
        super(name);

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

    @Override
    protected void updateValues() {
        x = Mth.clamp(x, 0, maxX());
        y = Mth.clamp(y, 0, maxY());
        width = Mth.clamp(width, 0, maxW());
        height = Mth.clamp(height, 0, maxH());
    }

    @Override
    void setDefaults() {

    }

    protected int maxH() {
        return frameEleH - y;
    }
    protected int maxW() {
        return frameEleW - x;
    }


}