package io.sedu.mc.parties.client.config;

import io.sedu.mc.parties.Parties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

public class DimConfig {

    public static HashMap<String, DimEntry> dimEntries = new HashMap<>();

    public static final DimEntry DEFAULT_ENTRY = new DimEntry(Items.BEDROCK, 0xFFFFFFF, 0);

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

    public static void entry(String loc, BiConsumer<ItemStack, Integer> action) {
        Objects.requireNonNull(action);
        DimEntry d = dimEntries.getOrDefault(loc, DEFAULT_ENTRY);
        action.accept(d.item, d.color);
    }


    public static int color(String loc) {
        return dimEntries.getOrDefault(loc, DEFAULT_ENTRY).color;
    }

    public static ItemStack item(String loc) {return dimEntries.getOrDefault(loc, DEFAULT_ENTRY).item;}

    public static void init() {


        //TODO: Only put entries if the dimension exists.
        addDimEntry("minecraft:overworld", "minecraft:grass_block", 0x7CDF9D, 0);
        addDimEntry("minecraft:the_nether", "minecraft:netherrack", 0xFFDA7A, 0);
        addDimEntry("minecraft:the_end", "minecraft:end_portal_frame", 0xCF7CDF, 0);
        addDimEntry("twilightforest:twilight_forest", "twilightforest:twilight_portal_miniature_structure", 0x58CBB6, 0);
    }

    private static void addDimEntry(String name, String loc, int color, int priority) {
        if (dimEntries.containsKey(name) && dimEntries.get(name).priority >= priority) return;
        ResourceLocation location = new ResourceLocation(loc);
        if (resourceInvalid(location)) return;
        dimEntries.put(name, new DimEntry(ForgeRegistries.ITEMS.getValue(location), color, priority));
    }

    private static boolean resourceInvalid(ResourceLocation loc) {
        if (!ForgeRegistries.ITEMS.containsKey(loc)) {
            Parties.LOGGER.debug("Error trying to load resource: " + loc + ". This may be alright!");
            return true;
        }
        return false;
    }


}
