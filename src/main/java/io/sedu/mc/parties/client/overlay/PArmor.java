package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PArmor extends RenderIconTextItem {

    public PArmor(String name, int x, int y, int textColor) {
        super(name, x, y, textColor);
    }



    @Override
    int getColor() {
        return 0xb8b9c4;
    }

    @Override
    public String getType() {
        return "Icon";
    }

    @Override
    void renderElement(PoseStack poseStack, ForgeIngameGui gui, Button b) {
        poseStack.pushPose();
        poseStack.scale(2f,2f,0);
        poseStack.translate(-.5f, 1, 0);
        setup(GUI_ICONS_LOCATION);
        RenderSystem.enableDepthTest();
        blit(poseStack, (b.x>>1)+4, b.y>>1, 34, 9, 9, 9);
        poseStack.popPose();
    }

    @Override
    void renderSelf(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        renderArmor(i, poseStack, gui, id.getArmor(), id.alpha);
    }

    @Override
    void renderMember(int i, ClientPlayerData id, ForgeIngameGui gui, PoseStack poseStack, float partialTicks) {
        if (id.isOnline)
            renderArmor(i, poseStack, gui, id.getArmor(), id.alpha);
    }

    void renderArmor(int i, PoseStack poseStack, ForgeIngameGui gui, int armor, float alpha){
        useAlpha(alpha);
        setup(Gui.GUI_ICONS_LOCATION);
        blit(poseStack, x(i), y(i), 34, 9, 9, 9);
        resetColor();

        if (notEditing() && withinBounds(x(i), y(i), x(i)+9, y(i)+9, 2)) {
            renderTooltip(poseStack, gui, 10, 0, "Armor: " + armor, 0xabfcff, 0x629b9e, 0xd1d1d1);
        }
        gui.getFont().draw(poseStack, String.valueOf(armor), tX(i), tY(i), color);
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + 11;
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }
}
