package io.sedu.mc.parties.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;


public class PartyOverlay {
    static int startX = 16;
    static int startY = 16;
    public static final IIngameOverlay HUD_PARTY = (gui, poseStack, partialTicks, width, height) -> {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
            renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack);
        }
    };

    public static void renderMember(int partyIndex, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack) {
        int yOffset = (partyIndex*64)+startY;
        gui.setupOverlayRenderState(true, false, id.getHead());
        GuiUtils.drawTexturedModalRect(poseStack, 16, yOffset+16, 32, 32, 32, 32, 1);
        gui.getFont().draw(poseStack, id.getName(), 56, yOffset+32, 0);
    }
}
