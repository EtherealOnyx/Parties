package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class ConfigOverlay extends RenderItem {

    private static boolean configMode = true;

    private static final IIngameOverlay CONFIG = (gui, poseStack, partialTicks, width, height) -> {
        if (HoverScreen.isActive()) {

        }
    };

    public ConfigOverlay(String name) {
        super(name);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        poseStack.translate(0,0,500);
    }
}
