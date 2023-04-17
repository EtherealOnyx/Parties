package io.sedu.mc.parties.client.overlay.anim;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.RenderItem;

public class HungerAnim extends AnimHandler {

    public float cur = 0f;
    public String hungerText = "";
    public static int type;

    public float oldH, curH = 0f;
    public float oldCur = 0f;
    public boolean hInc = true;

    public HungerAnim(int length, boolean enabled) {
        super(length, enabled);
        updateText();
    }

    public static RenderItem.SmallBound setTextType(int d) {
        type = d;
        ClientPlayerData.playerList.values().forEach(c -> c.getHunger(HungerAnim::updateText));
        return null;
    }

    public static int getTextType() {
        return type;
    }

    @Override
    void activateValues(Object... data) {
        float b1 = getPercent(oldCur);
        float b2 = getPercent((Float) data[0]);
        hInc = b1 < b2;
        oldH = b1;
        curH = b2;
        cur = (Float) data[0];
        updateText();
    }

    @Override
    public void activate(Object... data) {
        activateValues(data);
        animTime = length;
        active = true;
        animTickers.add(this);
    }

    @Override
    boolean tickAnim() {
        if (super.tickAnim()) {
            oldH = curH = 0f;
            oldCur = cur;
            return true;
        }
        return false;
    }

    private void updateText() {
        switch (type) {
            case 0 -> hungerText = (int) Math.ceil(cur) + "/" + 20;
            case 1 -> hungerText = String.valueOf((int) Math.ceil(cur));
            case 2 -> hungerText = (int) Math.ceil((cur / 20) * 100) + "%";
        }
    }

    public void reset(float pCur) {
        curH = getPercent(pCur);
        if (hInc) {
            if (curH < oldH) {
                hInc = false;
                animTime = 10;
            }
        } else {
            if (curH > oldH) {
                hInc = true;
                animTime = 10;
            }
        }

        //Slow: animTime = 15.
        if (animTime < 11) {
            animTime = 10;
        }

    }

    public float getPercent() {
        return cur / Math.max(cur, 20);
    }

    public static float getPercent(float cur) {
        return cur / Math.max(cur, 20);
    }

    public void checkHealth(float data) {
        if (data != cur) {
            if (active) {
                reset(data);
            } else {
                activate(data);
                return;
            }
            cur = data;
            updateText();
        }
    }
}

