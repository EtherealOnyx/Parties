package io.sedu.mc.parties.api.helper;

public class ColorAPI {

    public static boolean colorCycle = true;
    private static int color;
    private static float hue;

    public static float getR(int color) {
        return (float)(color >> 16 & 255) / 255.0F;
    }

    public static float getG(int color) {
        return (float)(color >> 8 & 255) / 255.0F;
    }

    public static float getB(int color) {
        return (float)(color & 255) / 255.0F;
    }

    public static int getRI(int color) {
        return (color >> 16 & 255);
    }

    public static int getGI(int color) {
        return (color >> 8 & 255);
    }

    public static int getBI(int color) {
        return (color & 255);
    }

    public static int getRainbowColor() {
        //S = .4F, L = .8F
        return color;
    }

    /**
     * HSBtoRGB method from Color.HSBtoRGB(hue, saturation, brightness) without alpha.
     */
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0F) {
            r = g = b = (int)(brightness * 255.0F + 0.5F);
        } else {
            float h = (hue - (float)Math.floor((double)hue)) * 6.0F;
            float f = h - (float)Math.floor((double)h);
            float p = brightness * (1.0F - saturation);
            float q = brightness * (1.0F - saturation * f);
            float t = brightness * (1.0F - saturation * (1.0F - f));
            switch ((int) h) {
                case 0 -> {
                    r = (int) (brightness * 255.0F + 0.5F);
                    g = (int) (t * 255.0F + 0.5F);
                    b = (int) (p * 255.0F + 0.5F);
                }
                case 1 -> {
                    r = (int) (q * 255.0F + 0.5F);
                    g = (int) (brightness * 255.0F + 0.5F);
                    b = (int) (p * 255.0F + 0.5F);
                }
                case 2 -> {
                    r = (int) (p * 255.0F + 0.5F);
                    g = (int) (brightness * 255.0F + 0.5F);
                    b = (int) (t * 255.0F + 0.5F);
                }
                case 3 -> {
                    r = (int) (p * 255.0F + 0.5F);
                    g = (int) (q * 255.0F + 0.5F);
                    b = (int) (brightness * 255.0F + 0.5F);
                }
                case 4 -> {
                    r = (int) (t * 255.0F + 0.5F);
                    g = (int) (p * 255.0F + 0.5F);
                    b = (int) (brightness * 255.0F + 0.5F);
                }
                case 5 -> {
                    r = (int) (brightness * 255.0F + 0.5F);
                    g = (int) (p * 255.0F + 0.5F);
                    b = (int) (q * 255.0F + 0.5F);
                }
            }
        }

        return r << 16 | g << 8 | b;
    }

    public static void tick() {
        if (!colorCycle) return;
        hue += 0.001f;
        if (hue > 1f)
            hue -= 1f;
        color = HSBtoRGB(hue, 0.5f, 0.75f);
    }
}
