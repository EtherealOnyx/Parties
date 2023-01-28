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
            renderText(gui, poseStack, String.valueOf(id.getXpLevel()), x(i) - String.valueOf(id.getLevelForced()).length()*3, y(i));
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        renderText(gui, poseStack, String.valueOf(id.getLevelForced()), x(i) - String.valueOf(id.getLevelForced()).length()*3, y(i));
    }

    private void renderText(ForgeIngameGui g, PoseStack p, String text, int x, int y) {
        g.getFont().draw(p, text, x, y, color);
    }
}