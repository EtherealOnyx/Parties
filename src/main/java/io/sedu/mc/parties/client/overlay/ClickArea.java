package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.util.RenderUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class ClickArea extends RenderItem {

    public ClickArea(String n) {
        super(n);
        clickArea = this;
        scale = 1f;
        zPos = 0;
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
    void setDefaults() {
        x = 7;
        y = 7;
        width = 159;
        height = 34;
    }


    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {

    }

    @Override
    public boolean isTabRendered() {
        return false;
    }

    @Override
    public void initItem() {
        item = (gui, poseStack, partialTicks, width, height) -> {
            //Default
            for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
                renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
            }
            //TODO: Add selected renderer.
        };
    }


    private void clickableOutline(PoseStack poseStack) {
        for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++)
            RenderUtils.borderRect(poseStack.last().pose(), -1, 2, l(i), t(i), width, height, 0x88AAFFFF);
    }


}
