package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PHealthText extends RenderSelfItem {

    int color;
    int absorbColor;
    int deadColor;

    public PHealthText(String name, int x, int y, int color, int absorbColor, int deadColor) {
        super(name, x, y);
        this.color = color;
        this.absorbColor = absorbColor;
        this.deadColor = deadColor;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack, partialTicks);
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (!id.isDead) {
            if (id.getAbsorb() > 0) {
                textCentered(i, gui, poseStack, (int)Math.ceil(id.getHealth()+id.getAbsorb()) + "/" + (int)id.getMaxHealth(), absorbColor);
            } else {
                textCentered(i, gui, poseStack, (int)Math.ceil(id.getHealth()) + "/" + (int)id.getMaxHealth(), color);
            }
        } else {
            textCentered(i, gui, poseStack, "Dead", deadColor);
        }
    }


}
