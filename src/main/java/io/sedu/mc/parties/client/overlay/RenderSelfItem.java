package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;

public abstract class RenderSelfItem extends RenderItem {



    public RenderSelfItem(String name) {
        super(name);
    }

    abstract void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks);

    abstract void renderSelf(ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack,
                             float partialTicks);


}
