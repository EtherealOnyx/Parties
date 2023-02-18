package io.sedu.mc.parties.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;
import io.sedu.mc.parties.client.overlay.gui.SettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.client.gui.ForgeIngameGui;

import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.notEditing;
import static io.sedu.mc.parties.client.overlay.gui.HoverScreen.withinBounds;
import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

public class PArmor extends RenderIconTextItem {

    public PArmor(String name, int x, int y, int textColor) {
        super(name, x, y, 9, 9, textColor, true);
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
        if (iconEnabled) {
            useAlpha(alpha);
            setup(Gui.GUI_ICONS_LOCATION);
            blit(poseStack, x(i), y(i), 34, 9, 9, 9);
            resetColor();
            if (notEditing() && withinBounds(x(i), y(i), x(i)+9, y(i)+9, 2)) {
                renderTooltip(poseStack, gui, 10, 0, "Armor: " + armor, 0xabfcff, 0x629b9e, 0xd1d1d1);
            }
        }
        if (textEnabled)
            text(gui, poseStack, String.valueOf(armor), tX(i), tY(i), color);
    }

    @Override
    protected int attachedX(int pOffset) {
        return x(pOffset) + 11;
    }

    @Override
    protected int attachedY(int pOffset) {
        return y(pOffset) + 1;
    }

    @Override
    protected ConfigOptionsList getConfigOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
        ConfigOptionsList c = super.getConfigOptions(s, minecraft, x, y, w, h);
        c.addTitleEntry("config.sedparties.title.general");
        c.addBooleanEntry("config.sedparties.name.display", isEnabled());
        c.addSliderEntry("config.sedparties.name.scale", 1, () -> 3, getScale(), true);
        c.addSliderEntry("config.sedparties.name.zpos", 0, () -> 10, zPos);
        c.addTitleEntry("config.sedparties.title.icon");
        c.addBooleanEntry("config.sedparties.name.idisplay", iconEnabled);
        c.addSliderEntry("config.sedparties.name.xpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX - (int)(width*scale)), this.x);
        c.addSliderEntry("config.sedparties.name.ypos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int)(height*scale)), this.y);
        c.addTitleEntry("config.sedparties.title.text");
        c.addBooleanEntry("config.sedparties.name.tdisplay", textEnabled);
        c.addBooleanEntry("config.sedparties.name.tshadow", textShadow);
        c.addColorEntry("config.sedparties.name.tcolor", color);
        c.addBooleanEntry("config.sedparties.name.tattached", textAttached);
        c.addSliderEntry("config.sedparties.name.xtpos", 0, () -> Math.max(0, Math.max(clickArea.r(0), frameX + frameW) - frameX), textX);
        c.addSliderEntry("config.sedparties.name.ytpos", 0, () -> Math.max(0, Math.max(clickArea.b(0), frameY + frameH) - frameY - (int)(minecraft.font.lineHeight*scale)), textY);
        return c;
    }

}
