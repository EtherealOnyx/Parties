package io.sedu.mc.parties.client.config;

public class Config {

    //Effect Config
    private static int maxBoth = 8;
    private static int maxBene = 8;
    private static int maxBad = 8;

    private static int rowBoth = 8;
    private static int rowBene = 8;
    private static int rowBad = 4;

    public static int beneColor = 0xA9E5FF;
    private static int badColor = 0xFFA9A9;
    private static int flashColor = 0xFFFFFF;

    public static int mA() {
        return maxBoth;
    }

    public static int mG() {
        return maxBene;
    }

    public static int cG() {
        return beneColor;
    }

    public static int cB() {
        return badColor;
    }

    public static int mB() {
        return maxBad;
    }

    public static int rA() {
        return rowBoth;
    }

    public static int rG() {
        return rowBene;
    }

    public static int rB() {
        return rowBad;
    }


    public static int cF() {
        return flashColor;
    }
}
