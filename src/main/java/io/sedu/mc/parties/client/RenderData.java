package io.sedu.mc.parties.client;

import java.util.ArrayList;

public class RenderData {
    public static int offsetX;
    public static int offsetY;
    public static int height;

    public static int[][] values = new int[3][4];
    public static int[][] cValues = new int[3][4];

    public static int[][] pos = new int[9][2];
    public static int[][] cPos = new int[9][2];





    public static void setData(int indexType, int x, int y, int width, int height) {
        values[indexType][0] = x;
        values[indexType][1] = y;
        values[indexType][2] = width;
        values[indexType][3] = height;

    }

    public static void setPos(int indexType, int x, int y) {
        pos[indexType][0] = x;
        pos[indexType][1] = y;
    }

    public static void refreshPos() {
        for (int i = 0; i < pos.length; i++) {
            cPos[i][0] = pos[i][0] + offsetX; //x
            cPos[i][1] = pos[i][1] + offsetY; //y
        }
    }

    public static void refresh() {
        for (int i = 0; i < values.length; i++) {
            cValues[i][0] = values[i][0] + offsetX; //left
            cValues[i][1] = values[i][0] + offsetX + values[i][2]; //right
            cValues[i][2] = values[i][1] + offsetY; //top
            cValues[i][3] = values[i][1] + offsetY + values[i][3]; //bottom
        }
    }

    public static void setOffset(int x, int y) {
        offsetX = x;
        offsetY = y;
    }



    private static int data(int type, int valueType) {
        return values[type][valueType];
    }

    public static int l(int type) {
        return cValues[type][0];
    }

    public static int r(int type) {
        return cValues[type][1];
    }

    public static int t(int type, int pIndex) {
        return cValues[type][2] + pIndex*height;
    }

    public static int b(int type, int pIndex) {
        return cValues[type][3] + pIndex*height;
    }

    public static int w(int type) {
        return values[type][2];
    }

    public static int h(int type) {
        return values[type][3];
    }

    public static int px(int type) {
        return cPos[type][0];
    }

    public static int py(int type, int pOffset) {
        return cPos[type][1] + pOffset*height;
    }

    public static void setDefaultData() {
        offsetX = 16;
        offsetY = 16;
        height = 56;
        //0: Health
        setData(0, 46, 29, 112, 10);
        //0 Head
        setPos(0, 8, 8);
        //1 Name
        setPos(1, 46, 9);
        //2 Armor
        setPos(2, 46, 19);
        //3 Armor Text
        setPos(3, 57, 20);
        //4 Chicken
        setPos(4, 132, 19);
        //5 Chicken Text
        setPos(5, 144, 20);
        //6 Absorption Heart
        setPos(6, 160, 30);
        //7 XP orb
        setPos(7, 16, 41);
        //8 Text
        setPos(8, 26, 42);

        //1 BG Box #1
        setData(1, 7, 41, 34, 11);
        //2 BG Box #2
        setData(2, 44, 7, 117, 34);
        refresh();
        refreshPos();
    }
}
