package io.sedu.mc.parties.client.overlay.gui;

import Util.Render;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.util.Mth;

public class SliderButton extends SmallButton {

    public static boolean clickReleased = false;
    int leftBound;
    int rightBound;
    int boundWidth;
    float xPos;
    float dragX;
    float oldPos;
    float curPos;
    boolean isDragging = false;
    int color;

    SliderButton.OnDrag onDragAction;
    SliderButton.OnRelease onReleaseAction;

    public SliderButton(int color, int w, SliderButton.OnDrag onDrag, SliderButton.OnRelease onRelease, OnTooltip pOnTooltip, float alpha) {
        super(0, 0, w, "", pButton -> {}, pOnTooltip, 0, 0, Render.getR(color), Render.getG(color), Render.getB(color), alpha);
        this.onDragAction = onDrag;
        this.onReleaseAction = onRelease;
        this.color = color;
    }

    public void updateX() {
        if (isDragging) {
            if (clickReleased) {
                clickReleased = false;
                this.onRelease(0,0);
                this.x = (int) (leftBound + boundWidth*curPos);
                return;
            }
            this.x = (int) (leftBound + boundWidth*xPos);
        } else
            this.x = (int) (leftBound + boundWidth*curPos);
    }

    public void renderBounds(Matrix4f pose) {
        Render.sizeRectNoA(pose, leftBound, y+4, boundWidth+width, 1, (color  & 0xfefefe) >> 1);
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBounds(pPoseStack.last().pose());
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        float oldXPos = xPos;
        dragX += pDragX;
        xPos = Mth.clamp((boundWidth*oldPos) + dragX, 0, boundWidth) / boundWidth;
        if (oldXPos != xPos)
            this.onDragAction.onDrag(xPos);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        isDragging = true;
        oldPos = curPos;
        xPos = oldPos;
        dragX = 0;
        clickReleased = false;
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        isDragging = false;
        curPos = Mth.clamp(xPos, 0, 1);
        xPos = 0;
        dragX = 0;
        this.onReleaseAction.onRelease(curPos);
    }

    public void updateValue(float v) {
        this.curPos = v;
    }

    public interface OnDrag {
        void onDrag(float percent);
    }

    public interface OnRelease {
        void onRelease(float percent);
    }

}
