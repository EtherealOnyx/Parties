package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PChickenText extends RenderSelfItem {
    int color;

    public PChickenText(String name, int x, int y, int color) {
        super(name, x, y);
        this.color = color;
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline) {
            render(i, gui, poseStack, String.valueOf(id.getHunger()));
        }
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        render(i, gui, poseStack, String.valueOf(id.getHungerForced()));

    }

    void render(int i, ForgeIngameGui gui, PoseStack poseStack, String text) {
        text(i, gui, poseStack, text, color);
    }
}
