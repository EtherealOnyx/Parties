package io.sedu.mc.parties.client.overlay;

import io.sedu.mc.parties.util.ColorUtils;
import io.sedu.mc.parties.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class ClickArea extends RenderItem {



    public ClickArea(String n, int x, int y, int w, int h) {
        super(n, x, y, w, h);
        clickArea = this;
    }

    @Override
    int getColor() {
        return 0;
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
        //Clickable
        //rect(i, poseStack,-2, 0, 0x88FFFFFF, 0x88FFFFFF);
    }

    @Override
    public boolean isTabRendered() {
        return false;
    }

    @Override
    public void initItem() {
        item = (gui, poseStack, partialTicks, width, height) -> {
            if (HoverScreen.isActive()) {
                //Config stuffies
                if (HoverScreen.arranging()) {
                    renderClickableArea(poseStack, gui, partialTicks);
                    return;
                }
                if (HoverScreen.moving()) {
                    renderFrame(poseStack, gui, partialTicks);
                    frameOutline(poseStack);
                    return;
                }
            }
            if (SettingsScreen.isActive()) {
                fullOutline(poseStack);
                return;
            }
            //Default
            for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
                renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
            }
        };
    }

    private void renderClickableArea(PoseStack poseStack, ForgeIngameGui gui, float partialTicks) {

        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++)
            rect(i, poseStack, -2, -2, ColorUtils.getRainbowColor() | 150 << 24);
    }

    private void renderFrame(PoseStack poseStack, ForgeIngameGui gui, float partialTicks) {
        int index = ClientPlayerData.playerOrderedList.size()-1;

        RenderUtils.rect(poseStack.last().pose(), -2, frameX, frameY, r(index),
                         b(index),
                         ColorUtils.getRainbowColor() | 150 << 24);
    }

    private void clickableOutline(PoseStack poseStack) {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++)
            RenderUtils.borderRect(poseStack.last().pose(), -1, 2, l(i), t(i), width, height, 0x88AAFFFF);
    }

    private void fullOutline(PoseStack poseStack) {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
            poseStack.translate(0,0,-5);
            RenderUtils.borderRect(poseStack.last().pose(), -1, 1, frameX + frameW*i, frameY + frameH*i,
                                   r(0) - frameX, b(0) - frameY,
                                   0xFFFFFFFF);
            rect(i, poseStack, 0, -1, ColorUtils.getRainbowColor() | 150 << 24);
            poseStack.translate(0,0,5);
        }

    }
    private void frameOutline(PoseStack poseStack) {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++)
            RenderUtils.borderRect(poseStack.last().pose(), -1, 1, frameX + frameW*i, frameY + frameH*i,
                                   frameW == 0 ? width + l(0) - frameX : r(0) - frameX,
                                   frameH == 0 ? height + t(0) - frameY: b(0) - frameY, 0xFFFFFFFF);
    }
}
