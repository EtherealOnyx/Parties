package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.isActive;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;

public class PArmor extends RenderSelfItem {

    public PArmor(String name, int x, int y) {
        super(name, x, y);

    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        useAlpha(id.alpha);
        setup(Gui.GUI_ICONS_LOCATION);
        blit(poseStack, x(i), y(i), 34, 9, 9, 9);
        resetColor();

        if (isActive() && withinBounds(x(i), y(i), x(i)+9, y(i)+9, 2)) {
            renderTooltip(poseStack, gui, 10, 0, "Armor: " + id.getArmor(), 0xabfcff, 0x629b9e, 0xd1d1d1);
        }
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderSelf(i, id, gui, poseStack,partialTicks);
    }
}
