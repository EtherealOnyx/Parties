package io.sedu.mc.parties.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;

public class DimConfig {

    public static HashMap<String, DimEntry> dimEntries = new HashMap<>();

    public static final DimEntry DEFAULT_ENTRY = new DimEntry("minecraft:bedrock", 0xFFFFFFF);

    static class DimEntry {
        ResourceLocation loc;
        TextureAtlasSprite tex;
        int color;

        static Random r = null;

        public DimEntry(String loc, int color) {
            this.color = color;
            this.loc = new ResourceLocation(loc);
        }

        public TextureAtlasSprite getSprite() {
            if (tex == null)
                try {
                    tex = Minecraft.getInstance().getBlockRenderer().getBlockModel(ForgeRegistries.BLOCKS.getValue(loc).defaultBlockState())
                                   .getQuads(ForgeRegistries.BLOCKS.getValue(loc).defaultBlockState(), Direction.NORTH, DimEntry.r).get(0).getSprite();
                } catch (Exception ignored){//help me
                    }
            return tex;
        }
    }

    public static void entry(String loc, BiConsumer<TextureAtlasSprite, Integer> action) {
        Objects.requireNonNull(action);
        DimEntry d = dimEntries.getOrDefault(loc, DEFAULT_ENTRY);

        action.accept(d.getSprite(), d.color);
    }


    public static TextureAtlasSprite sprite(String loc) {

        return dimEntries.getOrDefault(loc, DEFAULT_ENTRY).getSprite();
    }

    public static int color(String loc) {
        return dimEntries.getOrDefault(loc, DEFAULT_ENTRY).color;
    }

    public static void init() {
        DimEntry.r = new Random();
        DimEntry.r.setSeed(42L);


        //TODO: Only put entries if the dimension exists.
        dimEntries.put("minecraft:overworld", new DimEntry("minecraft:grass_block", 0x7CDF9D));
        dimEntries.put("minecraft:the_nether", new DimEntry("minecraft:warped_nylium", 0xFFDA7A));
        dimEntries.put("minecraft:the_end", new DimEntry("minecraft:obsidian", 0xCF7CDF));

        //if (ModList.get().isLoaded("")) For future mod dim support.
    }


}
