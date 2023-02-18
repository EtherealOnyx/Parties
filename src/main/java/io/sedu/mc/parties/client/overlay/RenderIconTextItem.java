package io.sedu.mc.parties.client.overlay;

public abstract class RenderIconTextItem extends RenderSelfItem {
    int color;
    int textX;
    int textY;
    boolean textAttached;
    boolean textEnabled = true;
    boolean iconEnabled = true;


    public RenderIconTextItem(String name, int x, int y, int textX, int textY, int textColor) {
        super(name, x, y);
        this.textX = textX;
        this.textY = textY;
        color = textColor;
        textAttached = false;

    }

    public RenderIconTextItem(String name, int x, int y, int textColor) {
        super(name, x, y);
        textX = 0;
        textY = 0;
        color = textColor;
        textAttached = true;
    }

    public RenderIconTextItem(String name, int x, int y, int width, int height, int textColor, boolean attached) {
        super(name, x, y, width, height);
        textX = 0;
        textY = 0;
        color = textColor;
        textAttached = attached;
    }

    public int tX(int pOffset) {
        return textAttached ? attachedX(pOffset)  : (int) ((frameX + textX + wOffset(pOffset))/scale);
    }

    public int tY(int pOffset) {
        return textAttached ? attachedY(pOffset)  : (int) ((frameY + textY + hOffset(pOffset))/scale);
    }

    protected abstract int attachedX(int pOffset);
    protected abstract int attachedY(int pOffset);

    @Override
    public void toggleIcon(boolean data) {
        iconEnabled = data;
    }

    @Override
    public void toggleText(boolean data) {
        textEnabled = data;
    }

    @Override
    public void toggleTextAttach(boolean data) {
        textAttached = data;
    }

    @Override
    public void setXTextPos(Integer data) {
        textX = data;
    }

    @Override
    public void setYTextPos(Integer data) {
        textY = data;
    }

    @Override
    public void setColor(int type, int data) {
        this.color = data;
    }

}
