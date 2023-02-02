package io.sedu.mc.parties.client.overlay.gui;

import io.sedu.mc.parties.client.overlay.effects.ClientEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BoundsEntry {
    public static HashMap<Integer, ArrayList<BoundsEntry>> elements = new HashMap<>();

    private int left;
    private int right;
    private int top;
    private int bottom;
    private int color;
    private int width;
    private int height;
    TextComponent tooltip;

    public BoundsEntry(int l, int r, int t, int b) {
        left = l;
        right = r;
        top = t;
        bottom = b;
    }

    public int w(){
        return width;
    }

    public int h(){
        return height;
    }

    public static void add(int type, int index, BoundsEntry b) {
        if (elements.get(type) == null) {
            ArrayList<BoundsEntry> list = new ArrayList<>();
            list.add(index, b);
            elements.put(type, list);
        } else {
            elements.get(type).remove(index);
            elements.get(type).add(index, b);
        }
    }

    public static void forEachBounded(int mouseX, int mouseY, Consumer<BoundsEntry> action) {
        Objects.requireNonNull(action);
        BoundsEntry.elements.values().forEach(boundsEntries -> boundsEntries.forEach(e -> {if (e.withinEntry(mouseX, mouseY)) action.accept(e);}));
    }

    public boolean withinEntry(int mouseX, int mouseY) {
        return mouseX > left && mouseX < right && mouseY > top && mouseY < bottom;
    }

    public TextComponent getTooltip() {
        return tooltip;
    }

    public void setTooltip(TextComponent t) {
        this.tooltip = t;
        width = Minecraft.getInstance().font.width(t);
        height = Minecraft.getInstance().font.lineHeight;
    }

    public void expand(int i) {
        left -= i;
        right += i;
        bottom += i;
        top -= i;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
