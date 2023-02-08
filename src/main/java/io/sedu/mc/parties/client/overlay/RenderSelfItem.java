package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.ForgeIngameGui;

public abstract class RenderSelfItem extends RenderItem {

    public static int selfIndex = 0;


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


            for (int i = 0; i < ClientPlayerData.playerOrderedList.size(); i++) {
                if (i == selfIndex)
                    renderSelf(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
                else
                    renderMember(i, ClientPlayerData.playerList.get(ClientPlayerData.playerOrderedList.get(i)), gui, poseStack, partialTicks);
            }
        };
    }

    public static void updateSelfIndex() {
        selfIndex = ClientPlayerData.playerOrderedList.indexOf(Minecraft.getInstance().player.getUUID());
        if (selfIndex == -1)
            selfIndex = 0; //Should never happen...
    }

}
