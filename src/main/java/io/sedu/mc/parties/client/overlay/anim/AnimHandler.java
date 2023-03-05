package io.sedu.mc.parties.client.overlay.anim;

import java.util.ArrayList;
import java.util.List;

public abstract class AnimHandler {


    static List<AnimHandler> animTickers = new ArrayList<>();
    //Generic data
    public final int length;
    public int animTime;
    public int id;
    public boolean active;
    public boolean enabled;

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

    public static void tick() {
        animTickers.removeIf(AnimHandler::tickAnim);
    }
}
