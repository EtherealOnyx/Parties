package io.sedu.mc.parties.client.config;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.client.overlay.RenderItem;

import java.util.ArrayList;
import java.util.List;

import static io.sedu.mc.parties.client.overlay.RenderItem.*;

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

    //TODO: This might throw errors if the preset isn't defined properly.
    public static int getX(int type) {
        assert type == 0 || type == 1;
        return currentEntry.get(type).xPos;
    }

    public static int getY(int type) {
        assert type == 0 || type == 1;
        return currentEntry.get(type).yPos;
    }

    public static double getScale(int type) {
        assert type == 0 || type == 1;
        return currentEntry.get(type).scale;
    }

    public static void refreshPos() {
        //Load position;
        RenderItem.updateFramePos(getX(0), getY(0), getX(1), getY(1), getScale(0), getScale(1));
    }

    private static boolean checkSave() {
        if (selfFrameX != getX(0)) {
            Parties.LOGGER.debug("selfX needs update.");
            return true;
        }
        if (selfFrameY != getY(0)) {
            Parties.LOGGER.debug("selfY needs update.");
            return true;
        }
        if (partyFrameX != getX(1)) {
            Parties.LOGGER.debug("partyX needs update.");
            return true;
        }
        if (partyFrameY != getY(1)) {
            Parties.LOGGER.debug("partyY needs update.");
            return true;
        }

        if (playerScale != getScale(0)) {
            Parties.LOGGER.debug("playerScale needs update.");
            return true;
        }

        if (partyScale != getScale(1)) {
            Parties.LOGGER.debug("partyScale needs update.");
            return true;
        }

        return false;

    }

    public static void save() {
        if (checkSave()) {
            updatePresetPos(0, selfFrameX, selfFrameY, playerScale);
            updatePresetPos(1, partyFrameX, partyFrameY, partyScale);
            Config.savePersistentPreset();
        }
    }

    private static void updatePresetPos(int type, int xPos, int yPos, double scale) {
        currentEntry.get(type).xPos = xPos;
        currentEntry.get(type).yPos = yPos;
        currentEntry.get(type).scale = scale;
    }

    public static List<PresetEntry> getEntries() {
        return currentEntry;
    }
}
