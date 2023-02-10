package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;

public class PLeaderIcon extends RenderItem {

    public PLeaderIcon(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isLeader()) {
            useAlpha(id.alpha);
            setup(partyPath);
            RenderSystem.enableDepthTest();
            blit(poseStack, x(i), y(i), 0, 0, 9, 9);
            resetColor();
            if (notEditing() && withinBounds(x(i), y(i), x(i)+9, y(i)+9, 2)) {
                renderTooltip(poseStack, gui, 10, 0, "Party Leader", 0xFFF2A9, 0x978B47, 0xFFE554);
            }
        }
    }

    @Override
    int getColor() {
        return 0xFFE554;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        poseStack.scale(2f,2f,0);
        poseStack.translate(-.5f, 0, 0);
        setup(partyPath);
        RenderSystem.enableDepthTest();
        blit(poseStack, (b.x>>1)+4, b.y>>1, 0, 0, 9, 9);
        poseStack.popPose();
    }
}
