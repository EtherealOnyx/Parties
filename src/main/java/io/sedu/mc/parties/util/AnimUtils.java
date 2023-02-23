package io.sedu.mc.parties.util;

public class AnimUtils {
    public static float animPos(int currTick, float partialTicks, boolean countingUp, int animLength, float scaleFactor) {
        return (float) (countingUp ? Math.pow((currTick+partialTicks)/animLength, scaleFactor) : Math.pow((currTick-partialTicks)/animLength, scaleFactor));
    }


}
