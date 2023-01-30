package io.sedu.mc.parties.client.overlay.anim;

public class HealthAnim extends AnimHandler {

    public float cur = 0f;
    public float max = 20f;
    public float absorb = 0f;

    public float oldL, oldR, curL, curR = 0f;
    public float oldCur = 0f;
    public float oldMax = 20f;
    public float oldAbsorb = 0f;
    public boolean iAnim = true;
    public boolean reset = false;

    public HealthAnim(int length, boolean enabled) {
        super(length, enabled);
    }

    @Override
    void activateValues(Object... data) {
        float b1 = getPercent(oldCur, oldMax, oldAbsorb);
        float b2 = getPercent((Float) data[0], (Float) data[1], (Float) data[2]);
        if (b1 < b2) {
            iAnim = true;
            oldL = b1;
            oldR = curR = curL = b2;
        } else {
            iAnim = false;
            oldL = curL = curR = b2;
            oldR = b1;
        }
        cur = (Float) data[0];
        max = (Float) data[1];
        absorb = (Float) data[2];
    }

    @Override
    boolean tickAnim() {
        if (super.tickAnim()) {
            oldL = curL = oldR = curR = 0f;
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
        System.out.println("Entered");
        float b2 = getPercent(pCur, pMax, pAbsorb);
        if (iAnim) {
            oldR = curR = curL = b2;
        } else {
            oldL = curL = curR = b2;
        }

        //Slow: animTime = 15. TODO: Add setting.
        animTime = 10;
    }

    public static float getPercent(float cur, float max, float absorb) {
        return cur / Math.max(absorb+cur, max);
    }

    public static float getPercentA(float cur, float max, float absorb) {
        return absorb / Math.max(absorb+cur, max);
    }
}
