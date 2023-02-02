package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public abstract class RenderSelfItem extends RenderItem {


    public RenderSelfItem(String name) {
        super(name);
    }

    public RenderSelfItem(String name, int x, int y) {
        super(name, x, y);
    }

    public RenderSelfItem(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }

    abstract void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);

    abstract void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                             float partialTicks);

    @Override
    public void initItem() {
        //If forced items are required and different (i.e, chicken)
        item = (gui, poseStack, partialTicks, width, height) -> {
            if (ClientPlayerData.playerOrderedList.size() == 0) return;
            renderSelf(0, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(0)), gui, poseStack, partialTicks);
            for (int i = 1; i < ClientPlayerData.playerOrderedList.size(); i++) {
                renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
            }
        };
    }

}
