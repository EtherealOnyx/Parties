package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.ForgeIngameGui;

public abstract class RenderSelfItem extends RenderItem {

    public static int selfIndex = 0;


    public RenderSelfItem(String name) {
        super(name);
    }

    abstract void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);

    abstract void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                             float partialTicks);

    public static boolean isSelf(int index) {
        return index == selfIndex;
    }

    public static void updateSelfIndex() {
        selfIndex = ClientPlayerData.playerOrderedList.indexOf(Minecraft.getInstance().player.getUUID());
        if (selfIndex == -1)
            selfIndex = 0; //Should never happen...
    }

}
