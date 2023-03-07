package io.sedu.mc.parties.client.config;

import io.sedu.mc.parties.Parties;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

public class DimConfig {

    public static HashMap<String, DimEntry> dimEntries = new HashMap<>();

    public static final DimEntry DEFAULT_ENTRY = new DimEntry(new ResourceLocation("textures/block/bedrock.png"), 0xFFFFFFF, 0);

    static class DimEntry {
        ResourceLocation loc;
        int color;
        int priority;

        public DimEntry(ResourceLocation loc, int color, int priority) {
            this.color = color;
            this.loc = loc;
            this.priority = priority;

        }
    }

    public static void entry(String loc, BiConsumer<ResourceLocation, Integer> action) {
        Objects.requireNonNull(action);
        DimEntry d = dimEntries.getOrDefault(loc, DEFAULT_ENTRY);
        action.accept(d.loc, d.color);
    }


    public static int color(String loc) {
        return dimEntries.getOrDefault(loc, DEFAULT_ENTRY).color;
    }

    public static ResourceLocation loc(String loc) {return dimEntries.getOrDefault(loc, DEFAULT_ENTRY).loc;}

    public static void init() {


        //TODO: Only put entries if the dimension exists.
        addDimEntry("minecraft:overworld", "textures/block/grass_block_side.png", 0x7CDF9D, 0);
        addDimEntry("minecraft:the_nether", "textures/block/warped_nylium_side.png", 0xFFDA7A, 0);
        addDimEntry("minecraft:the_end", "textures/block/obsidian.png", 0xCF7CDF, 0);
        addDimEntry("twilightforest:twilight_forest", "twilightforest:textures/block/hedge.png", 0x90C7B4, 0);
    }

    private static void addDimEntry(String name, String loc, int color, int priority) {
        if (dimEntries.containsKey(name) && dimEntries.get(name).priority >= priority) return;
        ResourceLocation location = new ResourceLocation(loc);
        if (resourceInvalid(location)) return;
        dimEntries.put(name, new DimEntry(location, color, priority));
    }

    private static boolean resourceInvalid(ResourceLocation loc) {
        try {
            Minecraft.getInstance().getResourceManager().getResource(loc);
        } catch (IOException e){
            Parties.LOGGER.debug("Error trying to load resource: " + loc + ". This may be alright!");
            return true;
        }
        return false;
    }


}
