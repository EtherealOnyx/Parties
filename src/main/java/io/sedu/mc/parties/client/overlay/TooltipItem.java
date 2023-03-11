package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public interface TooltipItem {

    void renderTooltip(PoseStack poseStack, ForgeIngameGui gui, int index, int mouseX, int mouseY);



}
