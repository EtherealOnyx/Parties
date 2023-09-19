package io.sedu.mc.parties.client.config;

import java.util.ArrayList;
import java.util.List;

public class PresetEntry {

    public static List<PresetEntry> currentEntry;

    String preset;
    int xPos;
    int yPos;
    double scale;

    public PresetEntry(String preset, int xPos, int yPos, double scale) {
        this.preset = preset;
        this.xPos = xPos;
        this.yPos = yPos;
        this.scale = scale;
    }

    public static boolean loadPreset(List<PresetEntry> entryList) {
        if (entryList == null) return false;
        currentEntry = entryList;
        return currentEntry.size() > 1;
    }

    public static char[] getMainPresetString() {
        return currentEntry.get(0).preset.toCharArray();
    }

    public static void updatePresetString(String presetString) {
        //Updates the default preset for both types which currently utilize the self preset.
        if (currentEntry == null) initEntry();
        currentEntry.get(0).preset = presetString;
    }

    private static void initEntry() {
        currentEntry = new ArrayList<>();
        currentEntry.add(new PresetEntry("", 8, 8, 1));
        currentEntry.add(new PresetEntry("", 8, 224, 0.5));
    }

    public static int getX(int type) {
        assert type == 0 || type == 1;
        return currentEntry.get(type).xPos;
    }

    public static int getY(int type) {
        return currentEntry.get(type).yPos;
    }
}
