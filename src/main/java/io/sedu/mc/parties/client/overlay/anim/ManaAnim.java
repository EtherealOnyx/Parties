package io.sedu.mc.parties.client.overlay.anim;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.RenderItem;

public class ManaAnim extends AnimBarHandler {

    public static int type;

    public float oldH, curH = 0f;
    public float oldCur = 0f;
    public int oldMax = 100;
    public boolean hInc = true;

    public ManaAnim(int length, boolean enabled) {
        super(length);
        updateText();
    }

    public static RenderItem.SmallBound setTextType(int d) {
        type = d;
        ClientPlayerData.playerList.values().forEach(c -> c.getMana(ManaAnim::updateText));
        return null;
    }

    public static int getTextType() {
        return type;
    }

    @Override
    void activateValues(Object... data) {
        float b1 = getPercent(oldCur, oldMax);
        float b2 = getPercent((Float) data[0], (Integer) data[1]);
        hInc = b1 < b2;
        oldH = b1;
        curH = b2;
        cur = (Float) data[0];
        max = (Integer) data[1];
        updateText();
    }

    @Override
    int getType() {
        return type;
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
            oldMax = (int) max;
            return true;
        }
        return false;
    }

    public void reset(float pCur, int pMax) {
        curH = getPercent(pCur, pMax);
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
        return cur / Math.max(cur, max);
    }

    public static float getPercent(float cur, int max) {
        return cur / Math.max(cur, max);
    }

    public void checkHealth(float data) {
        if (data != cur) {
            if (active) {
                reset(data, (int) max);
            } else {
                activate(data, max);
                return;
            }
            cur = data;
            updateText();
        }
    }

    public void checkMax(int data) {
        if (data != max) {
            if (active) {
                reset(cur, data);
            } else {
                activate(cur, data);
                return;
            }
            max = data;
            updateText();
        }
    }

    public void checkValues(float currentMana, int maxMana) {
        if (currentMana != cur || maxMana != max) {
            if (active) {
                reset(currentMana, maxMana);
            } else {
                activate(currentMana, maxMana);
                return;
            }
            max = maxMana;
            cur = currentMana;
            updateText();
        }
    }
}

