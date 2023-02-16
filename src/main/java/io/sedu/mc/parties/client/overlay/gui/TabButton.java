package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class TabButton extends Button {

    private static final Component EMPTY = new TextComponent("");
    private final Action action;
    public Component type;
    int index;

    public TabButton(int index, int pX, int pY, int pWidth, int pHeight, OnPress pOnPress, OnTooltip pOnTooltip, Action i, String type) {
        super(pX, pY, pWidth, pHeight, EMPTY, pOnPress, pOnTooltip);
        this.type = new TextComponent(type);
        action = i;
        this.index = index;
    }


    public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        //super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);

        if (index == SettingsScreen.selEle) {
            action.onSelect(poseStack, this);
            if (this.isHoveredOrFocused()) {
                this.renderToolTip(poseStack, pMouseX, pMouseY);
                return;
            }
            return;
        }

        if (this.isHoveredOrFocused()) {
            action.onHover(poseStack, this);
            RenderSystem.enableDepthTest();
            this.renderToolTip(poseStack, pMouseX, pMouseY);
            return;
        }
        action.onRender(poseStack, this);

    }

    public ConfigOptionsList getOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h) {
        return action.getOptions(s, minecraft, x, y, w, h);
    }


    public interface Action {
        void onRender(PoseStack p, TabButton b);
        void onHover(PoseStack p, TabButton b);
        void onSelect(PoseStack p, TabButton b);

        ConfigOptionsList getOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h);
    }
}
