package io.sedu.mc.parties.client.overlay.anim;

import java.text.DecimalFormat;

public abstract class AnimBarHandler extends AnimHandlerBase {



    public static final DecimalFormat DF = new DecimalFormat("##.#");

    public int id;

    public float cur = 0f;
    public float max = 20f;
    public float absorb = 0f;

    public String displayText = "";

    AnimBarHandler(int length) {
        super(length);
        animTime = 0;
    }

    abstract int getType();

    protected void updateText() {
        switch (getType()) {
            case 0 -> displayText = DF.format(cur + absorb) + "/" + DF.format(max);
            case 1 -> {
                if (absorb > 0)
                    displayText = DF.format(cur) + " (" + DF.format(absorb) + ")";
                else
                    displayText = DF.format(cur);
            }
            case 2 -> displayText = DF.format(((cur + absorb) / max) * 100) + "%";
            case 3 -> displayText = DF.format(cur) + (absorb > 0 ? "*" : "");
        }
    }

    public static void tick() {
        animTickers.removeIf(AnimHandlerBase::tickAnim);
    }
}
