package io.sedu.mc.parties.client.overlay.anim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class AnimHandler {


    static List<AnimHandler> animTickers = new ArrayList<>();
    public static final DecimalFormat DF = new DecimalFormat("##.#");
    //Generic data
    public final int length;
    public int animTime;
    public int id;
    public boolean active;
    public boolean enabled;

    public float cur = 0f;
    public float max = 20f;
    public float absorb = 0f;

    public String displayText = "";

    AnimHandler(int length, boolean enabled) {
        this.length = length;
        this.enabled = enabled;
        animTime = 0;
    }

    boolean tickAnim() {

        animTime -= 1;
        if (animTime <= 0) {
            animTime = 0;
            active = false;
            return true;
        }
        return false;
    }

    abstract void activateValues(Object... data);

    abstract int getType();


    public void activate(Object... data) {
        if (animTime != 0) {
            //Already tracked
            activateValues(data); //Reset anim with new data.
            animTime = length;
            return;
        }
        activateValues(data);
        animTime = length;
        active = true;
        animTickers.add(this);
    }

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
        animTickers.removeIf(AnimHandler::tickAnim);
    }
}
