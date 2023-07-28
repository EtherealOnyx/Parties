package io.sedu.mc.parties.client.overlay;

import io.sedu.mc.parties.client.overlay.gui.ConfigOptionsList;

import java.util.ArrayList;

public abstract class RenderIconTextItem extends RenderSelfItem {
    int color;
    int textX;
    int textY;
    boolean textAttached;




    public RenderIconTextItem(String name) {
        super(name);
    }

    public int tX(int pOffset) {
        return textAttached ? attachedX(pOffset)  : (int) (((pOffset == 0 ? selfFrameX : partyFrameX) + textX + wOffset(pOffset))/scale);
    }

    public int tY(int pOffset) {
        return textAttached ? attachedY(pOffset)  : (int) (((pOffset == 0 ? selfFrameY : partyFrameY) + textY + hOffset(pOffset))/scale);
    }

    protected abstract int attachedX(int pOffset);
    protected abstract int attachedY(int pOffset);



    public SmallBound toggleTextAttach(boolean data) {
        textAttached = data;
        return null;
    }


    public SmallBound setXTextPos(Integer data) {
        textX = data;
        return null;
    }


    public SmallBound setYTextPos(Integer data) {
        textY = data;
        return null;
    }

    @Override
    public SmallBound setColor(int type, int data) {
        this.color = data;
        return null;
    }

    @Override
    public int getColor(int type) {return color;}

    protected void toggleTextAttach(ArrayList<ConfigOptionsList.Entry> entries) {
            entries.forEach(entry -> entry.setVisible(!textAttached));
    }
}
