package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class PArmor extends RenderSelfItem {

    public PArmor(String name, int x, int y) {
        super(name, x, y);

    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        useAlpha(id.alpha);
        setup(Gui.GUI_ICONS_LOCATION);
        gui.blit(poseStack, x(i), y(i), 34, 9, 9, 9);
        resetColor();
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack,partialTicks);
    }
}
