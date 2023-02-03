package io.sedu.mc.parties.client.overlay.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class HoverScreen extends Screen {

    private static int mouseX;
    private static int mouseY;
    private static boolean active = false;
    int key;

    //public boolean rendered;
    public HoverScreen(int value) {
        super(new TextComponent("Mouse Hover"));
        this.key = value;
    }

    public static boolean withinBounds(int left, int top, int right, int bottom, int expand) {
        return mouseX > left - expand && mouseX < right + expand && mouseY > top - expand && mouseY < bottom + expand;
    }

    public static int mouseX() {
        return mouseX;
    }

    public static int mouseY() {
        return mouseY;
    }

    public static void updateValues(int x, int y) {
        mouseX = x;
        mouseY = y;
    }

    public static void disable() {
        active = false;
        mouseX = -1;
        mouseY = -1;
    }

    public static void activate() {
        active = true;
        mouseX = -1;
        mouseY = -1;
    }

    public static boolean isActive() {
        return active;
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode != key) {
            active = false;
            Minecraft.getInstance().setScreen(null);
        }
        return true;
    }
}
