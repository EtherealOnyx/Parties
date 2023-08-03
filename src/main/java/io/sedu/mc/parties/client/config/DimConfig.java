package io.sedu.mc.parties.client.config;

import io.sedu.mc.parties.Parties;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class DimConfig {

    public static HashMap<String, DimEntry> dimEntries = new HashMap<>();
    public static HashMap<String, DimEntry> missingEntries = new HashMap<>();

    public static final DimEntry DEFAULT_ENTRY = new DimEntry(Items.BEDROCK, 0xFFFFFFF, 0);

    public static void checkDim(String resource) {
        Parties.LOGGER.debug("Checking for dim called {}.", resource);
        if (dimEntries.containsKey(resource) || missingEntries.containsKey(resource)) {
            Parties.LOGGER.debug("Dimension found, no action needed.");
        } else {
            Parties.LOGGER.debug("Dimension not found, storing in config/dims/missing.json");
            //Add missing entries.
            Config.forEachMissing(list -> list.forEach(DimConfig::addMissingEntry));
            //Add new entry.
            missingEntries.put(resource, new DimEntry(Items.BEDROCK, 0xFFFFFF, -1));
            //Save missingEntries to file.
            Config.saveMissingDims();
        }
    }



    static class DimEntry {
        ItemStack item;
        int color;
        int priority;

        public DimEntry(Item loc, int color, int priority) {
            this.color = color;
            this.item = new ItemStack(loc);
            this.priority = priority;

        }
    }

    static class DimEntryConfig {

        String dimension;
        String item;
        int color;
        int priority;

        public DimEntryConfig(String dimension, String item, int color, int priority) {
            this.dimension = dimension;
            this.item = item;
            this.color = color;
            this.priority = priority;
        }



    }

    public static void entry(String loc, BiConsumer<ItemStack, Integer> action) {
        Objects.requireNonNull(action);
        DimEntry d = dimEntries.getOrDefault(loc, missingEntries.getOrDefault(loc, DEFAULT_ENTRY));
        action.accept(d.item, d.color);
    }


    public static int color(String loc) {
        return dimEntries.getOrDefault(loc, missingEntries.getOrDefault(loc, DEFAULT_ENTRY)).color;
    }

    public static ItemStack item(String loc) {return dimEntries.getOrDefault(loc, missingEntries.getOrDefault(loc, DEFAULT_ENTRY)).item;}

    public static void init() {
        List<DimEntryConfig> dims = getDefaultDims();
        dims.forEach(DimConfig::addDimEntry);
        Config.saveDefaultDims(dims);
        //Load custom dim entries as well.
        Config.forEachDimFile(list -> list.forEach(DimConfig::addDimEntry));
        //Reload missing entries if present as well!
        missingEntries.clear();
        Config.forEachMissing(list -> list.forEach(DimConfig::addMissingEntry));
        //Save missing entries.
        Config.saveMissingDims();
    }

    public static List<DimEntryConfig> getDefaultDims() {
        List<DimEntryConfig> dims = new ArrayList<>();
        dims.add( new DimEntryConfig("minecraft:overworld", "minecraft:grass_block", 0x7CDF9D, -1));
        dims.add( new DimEntryConfig("minecraft:the_nether", "minecraft:netherrack", 0xFFDA7A, -1));
        dims.add( new DimEntryConfig("minecraft:the_end", "minecraft:end_portal_frame", 0xCF7CDF, -1));
        dims.add( new DimEntryConfig("twilightforest:twilight_forest", "twilightforest:twilight_portal_miniature_structure", 0x58CBB6, -1));
        dims.add( new DimEntryConfig("rftoolsdim:dim", "rftoolsdim:dimensional_cross_block", 0xbbdddd, -2));
        dims.add( new DimEntryConfig("rftoolsdim:dim", "rftoolsutility:matter_transmitter", 0xbbdddd, -1));
        return dims;
    }

    public static void reload() {
        dimEntries.clear();
        init();
        Minecraft.getInstance().player.displayClientMessage(new TranslatableComponent("messages.sedparties.config.reloadsuccess"), true);
    }

    private static void addDimEntry(DimEntryConfig entry) {
        if (!ModList.get().isLoaded(entry.dimension.substring(0, entry.dimension.indexOf(':')))) return;
        if (dimEntries.containsKey(entry.dimension) && dimEntries.get(entry.dimension).priority >= entry.priority) return;
        ResourceLocation location = new ResourceLocation(entry.item);
        if (resourceInvalid(location)) return;
        dimEntries.put(entry.dimension, new DimEntry(ForgeRegistries.ITEMS.getValue(location), entry.color, entry.priority));
    }
    private static void addMissingEntry(DimEntryConfig entry) {
        if (!ModList.get().isLoaded(entry.dimension.substring(0, entry.dimension.indexOf(':')))) return;
        if (dimEntries.containsKey(entry.dimension)) return; //Entries outside missing.json will always have higher priority.
        ResourceLocation location = new ResourceLocation(entry.item);
        if (resourceInvalid(location)) {
            Parties.LOGGER.warn("[Parties] DimEntry {} had an invalid item location. Changing to minecraft:bedrock.", entry.dimension);
            location = new ResourceLocation("minecraft:bedrock");
        }
        missingEntries.put(entry.dimension, new DimEntry(ForgeRegistries.ITEMS.getValue(location), entry.color, entry.priority));
    }

    private static boolean resourceInvalid(ResourceLocation loc) {
        if (!ForgeRegistries.ITEMS.containsKey(loc)) {
            Parties.LOGGER.warn("[Parties] Failed to load dimension item: '" + loc + "'. This may be alright!");
            return true;
        }
        return false;
    }


}
