package io.sedu.mc.parties.client.overlay.anim;

import java.util.ArrayList;
import java.util.List;

public abstract class AnimHandlerBase {
    static List<AnimHandlerBase> animTickers = new ArrayList<>();
    //Generic data
    public boolean active;
    public final int length;
    public int animTime;

    AnimHandlerBase(int length) {
        this.length = length;
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
}
