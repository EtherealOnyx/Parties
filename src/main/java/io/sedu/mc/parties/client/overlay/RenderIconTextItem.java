package io.sedu.mc.parties.client.overlay;

public abstract class RenderIconTextItem extends RenderSelfItem {
    int color;
    int textX;
    int textY;
    boolean textAttached;
    boolean textEnabled;


    public RenderIconTextItem(String name, int x, int y, int textX, int textY, int textColor) {
        super(name, x, y);
        this.textX = textX;
        this.textY = textY;
        color = textColor;
        textAttached = false;
        textEnabled = true;
    }

    public RenderIconTextItem(String name, int x, int y, int textColor) {
        super(name, x, y);
        textX = 0;
        textY = 0;
        color = textColor;
        textAttached = true;
        textEnabled = true;
    }

    public RenderIconTextItem(String name, int x, int y, int width, int height, int textColor, boolean attached) {
        super(name, x, y, width, height);
        textX = 0;
        textY = 0;
        color = textColor;
        textAttached = attached;
        textEnabled = true;
    }

    public int tX(int pOffset) {
        return textAttached ? attachedX(pOffset)  : textX+frameX + wOffset(pOffset);
    }

    public int tY(int pOffset) {
        return textAttached ? attachedY(pOffset)  : textY+frameY + hOffset(pOffset);
    }

    protected abstract int attachedX(int pOffset);
    protected abstract int attachedY(int pOffset);

}
