package io.sedu.mc.parties.client.overlay.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.sedu.mc.parties.client.overlay.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class TabButton extends Button {

    private static final Component EMPTY = new TextComponent("");
    private final Action action;
    int index;
    boolean isCut;

    public TabButton(int index, int pX, int pY, int pWidth, int pHeight, OnPress pOnPress, OnTooltip pOnTooltip, Action i) {
        super(pX, pY, pWidth, pHeight, EMPTY, pOnPress, pOnTooltip);
        action = i;
        this.index = index;
        isCut = false;
    }

    public TabButton(int index, int pX, int pY, int pWidth, int pHeight, OnPress pOnPress, OnTooltip pOnTooltip, Action i, boolean isCut) {
        super(pX, pY, pWidth, pHeight, EMPTY, pOnPress, pOnTooltip);
        action = i;
        this.index = index;
        this.isCut = isCut;
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

    public ResourceLocation getInnerBg() {
        return action.getInnerBackground();
    }

    public RenderItem.ItemBound getBounds() {
        return action.getItemBound();
    }


    public interface Action {
        void onRender(PoseStack p, TabButton b);
        void onHover(PoseStack p, TabButton b);
        void onSelect(PoseStack p, TabButton b);
        ConfigOptionsList getOptions(SettingsScreen s, Minecraft minecraft, int x, int y, int w, int h);
        ResourceLocation getInnerBackground();
        RenderItem.ItemBound getItemBound();

    }
}
