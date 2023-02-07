package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PLevelText extends RenderSelfItem {
    int color;

    public PLevelText(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderText(gui, poseStack, String.valueOf(id.getXpLevel()), x(i) - String.valueOf(id.getXpLevel()).length()*3, y(i));
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        renderText(gui, poseStack, String.valueOf(id.getLevelForced()), x(i) - (gui.getFont().width(String.valueOf(id.getLevelForced()))>>1), y(i));
    }

    private void renderText(ForgeIngameGui g, PoseStack poseStack, String s, int x, int y) {
        g.getFont().draw(poseStack, s, (float)(x + 1), (float)y, 0);
        g.getFont().draw(poseStack, s, (float)(x - 1), (float)y, 0);
        g.getFont().draw(poseStack, s, (float)x, (float)(y + 1), 0);
        g.getFont().draw(poseStack, s, (float)x, (float)(y - 1), 0);
        g.getFont().draw(poseStack, s, (float)x, (float)y, 8453920); //8453920
    }
}