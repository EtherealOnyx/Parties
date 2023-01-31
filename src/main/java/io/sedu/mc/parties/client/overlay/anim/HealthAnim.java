package io.sedu.mc.parties.client.overlay.anim;

public class HealthAnim extends AnimHandler {

    public float cur = 0f;
    public float max = 20f;
    public float absorb = 0f;

    public float oldH, curH, oldA, curA = 0f;
    public float oldCur = 0f;
    public float oldMax = 20f;
    public float oldAbsorb = 0f;
    public boolean hInc = true;
    public boolean aInc = true;
    public boolean reset = false;

    public HealthAnim(int length, boolean enabled) {
        super(length, enabled);
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
            }
            cur = pCur;
            max = pMax;
            absorb = pAbsorb;
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



        //Slow: animTime = 15. TODO: Add setting.
        if (animTime < 11) {
            animTime = 10;
        }

    }

    public static float getPercent(float cur, float max, float absorb) {
        return cur / Math.max(absorb+cur, max);
    }

    public static float getPercentE(float cur, float max, float absorb) {
        return (cur+absorb) / Math.max(absorb+cur, max);
    }

    public static float getPercentA(float cur, float max, float absorb) {
        return absorb / Math.max(absorb+cur, max);
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
            }
            cur = data;
        }
    }

    public void checkAbsorb(float data) {
        if (data != absorb) {
            if (active) {
                reset(cur, max, data);
            } else {
                activate(cur, max, data);
            }
            absorb = data;
        }
    }

    public void checkMax(float data) {
        if (data != max) {
            if (active) {
                reset(cur, data, absorb);
            } else {
                activate(cur, data, absorb);
            }
            max = data;
        }
    }
}
