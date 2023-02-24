package io.sedu.mc.parties.client.overlay;

import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;

import java.util.ArrayList;

public abstract class RenderIconTextItem extends RenderSelfItem {
    int color;
    int textX;
    int textY;
    boolean textAttached;
    boolean textEnabled;
    boolean iconEnabled;




    public RenderIconTextItem(String name) {
        super(name);
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

    protected void toggleTextAttach(ArrayList<ConfigOptionsList.Entry> entries) {
            entries.forEach(entry -> entry.setVisible(!textAttached));
    }
}
