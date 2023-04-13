package io.sedu.mc.parties.client.overlay.anim;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.RenderItem;

public class HealthAnim extends AnimHandler {

    public float cur = 0f;
    public float max = 20f;
    public float absorb = 0f;
    public String healthText = "";
    public static int type;

    public float oldH, curH, oldA, curA = 0f;
    public float oldCur = 0f;
    public float oldMax = 20f;
    public float oldAbsorb = 0f;
    public boolean hInc = true;
    public boolean aInc = true;

    public HealthAnim(int length, boolean enabled) {
        super(length, enabled);
        updateText();
    }

    public static RenderItem.SmallBound setTextType(int d) {
        type = d;
        ClientPlayerData.playerList.values().forEach(c -> c.getHealth().updateText());
        return null;
    }

    public static int getTextType() {
        return type;
    }

    @Override
    void activateValues(Object... data) {
        float b1 = getPercent(oldCur, oldMax, oldAbsorb);
        float b2 = getPercent((Float) data[0], (Float) data[1], (Float) data[2]);
        hInc = b1 < b2;
        oldH = b1;
        curH = b2;

        if ((Float) data[2] > 0) {
            b1 = getPercentE(oldCur, oldMax, oldAbsorb);
            b2 = getPercentE((Float) data[0], (Float) data[1], (Float) data[2]);
            aInc = b1 < b2;
            oldA = b1;
            curA = b2;
        } else {
            oldA = curA = 0;
        }
        cur = (Float) data[0];
        max = (Float) data[1];
        absorb = (Float) data[2];
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
            oldMax = max;
            oldAbsorb = absorb;
            return true;
        }
        return false;
    }

    public void checkAnim(float pCur, float pMax, float pAbsorb) {
        if (pCur != cur || pMax != max || pAbsorb != absorb) {
            if (active) {
                reset(pCur, pMax, pAbsorb);
            } else {
                activate(pCur, pMax, pAbsorb);
                return;
            }
            cur = pCur;
            max = pMax;
            absorb = pAbsorb;
            updateText();
        }
    }

    private void updateText() {
        switch (type) {
            case 0 -> healthText = (int) Math.ceil(cur + absorb) + "/" + (int) max;
            case 1 -> {
                if (absorb > 0)
                    healthText = (int) Math.ceil(cur) + " (" + (int) Math.ceil(absorb) + ")";
                else
                    healthText = String.valueOf((int) Math.ceil(cur));
            }
            case 2 -> healthText = (int) Math.ceil(((cur + absorb) / max) * 100) + "%";
        }
    }

    public void reset(float pCur, float pMax, float pAbsorb) {
        curH = getPercent(pCur, pMax, pAbsorb);
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

        if (pAbsorb > 0) {
            curA = getPercentE(pCur, pMax, pAbsorb);
        } else {
            if (curA == oldA) {
                oldAbsorb = pAbsorb;
            }
        }



        //Slow: animTime = 15.
        if (animTime < 11) {
            animTime = 10;
        }

    }

    public float getPercent() {
        return cur / Math.max(absorb+cur, max);
    }

    public float getPercentA() {
        return absorb / Math.max(absorb+cur, max);
    }

    public static float getPercent(float cur, float max, float absorb) {
        return cur / Math.max(absorb+cur, max);
    }

    public static float getPercentE(float cur, float max, float absorb) {
        return (cur+absorb) / Math.max(absorb+cur, max);
    }

    public boolean effH() {
        return (absorb + cur) > max;
    }

    public boolean effHOld() {
        return (oldAbsorb + oldCur) > oldMax;
    }

    public void checkHealth(float data) {
        if (data != cur) {
            if (active) {
                reset(data, max, absorb);
            } else {
                activate(data, max, absorb);
                return;
            }
            cur = data;
            updateText();
        }
    }

    public void checkAbsorb(float data) {
        if (data != absorb) {
            if (active) {
                reset(cur, max, data);
            } else {
                activate(cur, max, data);
                return;
            }
            absorb = data;
            updateText();
        }
    }

    public void checkMax(float data) {
        if (data != max) {
            if (active) {
                reset(cur, data, absorb);
            } else {
                activate(cur, data, absorb);
                return;
            }
            max = data;
            updateText();
        }
    }

    public float getAbsorb() {
        return absorb;
    }
}
