package io.sedu.mc.parties.api.helper;

import com.mojang.blaze3d.platform.NativeImage;
import io.sedu.mc.parties.lib.ct.ColorThief;
import io.sedu.mc.parties.lib.ct.RGBUtil;
import io.sedu.mc.parties.mixinaccessors.MainImageAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ColorAPI {



    private static boolean colorCycle = true;
    private static int color;
    private static float hue;


    /**
     * Grabs the Red value of an integer and converts it to a float.
     * @param color The color that is grabbed from.
     * @return A float that holds the red value of the provided integer.
     */
    public static float getR(int color) {
        return (float)(color >> 16 & 255) / 255.0F;
    }

    /**
     * Grabs the Green value of an integer and converts it to a float.
     * @param color The color that is grabbed from.
     * @return A float that holds the green value of the provided integer.
     */
    public static float getG(int color) {
        return (float)(color >> 8 & 255) / 255.0F;
    }

    /**
     * Grabs the Blue value of an integer and converts it to a float.
     * @param color The color that is grabbed from.
     * @return A float that holds the blue value of the provided integer.
     */
    public static float getB(int color) {
        return (float)(color & 255) / 255.0F;
    }

    /**
     * Grabs the Red value of an integer and converts it to a range of 0-255.
     * @param color The color that is grabbed from.
     * @return An integer that holds the red value of the provided integer (from 0-255).
     */
    public static int getRI(int color) {
        return (color >> 16 & 255);
    }

    /**
     * Grabs the Green value of an integer and converts it to a range of 0-255.
     * @param color The color that is grabbed from.
     * @return An integer that holds the green value of the provided integer (from 0-255).
     */
    public static int getGI(int color) {
        return (color >> 8 & 255);
    }

    /**
     * Grabs the Blue value of an integer and converts it to a range of 0-255.
     * @param color The color that is grabbed from.
     * @return An integer that holds the blue value of the provided integer (from 0-255).
     */
    public static int getBI(int color) {
        return (color & 255);
    }

    /**
     * Gets an integer that cycles colors automatically when ColorAPI.colorCycle is true.
     * @return The color in integer form.
     */
    public static int getRainbowColor() {
        return color;
    }

    /**
     * HSBtoRGB method from Color.HSBtoRGB(hue, saturation, brightness) without alpha.
     */
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0F) {
            r = g = b = (int)(brightness * 255.0F + 0.5F);
        } else {
            float h = (hue - (float)Math.floor((double)hue)) * 6.0F;
            float f = h - (float)Math.floor((double)h);
            float p = brightness * (1.0F - saturation);
            float q = brightness * (1.0F - saturation * f);
            float t = brightness * (1.0F - saturation * (1.0F - f));
            switch ((int) h) {
                case 0 -> {
                    r = (int) (brightness * 255.0F + 0.5F);
                    g = (int) (t * 255.0F + 0.5F);
                    b = (int) (p * 255.0F + 0.5F);
                }
                case 1 -> {
                    r = (int) (q * 255.0F + 0.5F);
                    g = (int) (brightness * 255.0F + 0.5F);
                    b = (int) (p * 255.0F + 0.5F);
                }
                case 2 -> {
                    r = (int) (p * 255.0F + 0.5F);
                    g = (int) (brightness * 255.0F + 0.5F);
                    b = (int) (t * 255.0F + 0.5F);
                }
                case 3 -> {
                    r = (int) (p * 255.0F + 0.5F);
                    g = (int) (q * 255.0F + 0.5F);
                    b = (int) (brightness * 255.0F + 0.5F);
                }
                case 4 -> {
                    r = (int) (t * 255.0F + 0.5F);
                    g = (int) (p * 255.0F + 0.5F);
                    b = (int) (brightness * 255.0F + 0.5F);
                }
                case 5 -> {
                    r = (int) (brightness * 255.0F + 0.5F);
                    g = (int) (p * 255.0F + 0.5F);
                    b = (int) (q * 255.0F + 0.5F);
                }
            }
        }

        return r << 16 | g << 8 | b;
    }

    /**
     * Grabs the dominant color of the provided item.
     * @param item The item stack to search.
     * @return An integer holding the dominant color of the provided item. Returns 0 if the item is invalid.
     */
    public static int getDomColor(ItemStack item) {
        Item i = item.getItem();
        TextureAtlasSprite texture;
        if (i instanceof BlockItem b) {
            BlockState state = b.getBlock().defaultBlockState();
            texture = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state).getParticleIcon();
        } else {
            texture = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(item).getParticleIcon();
        }

        if (texture instanceof MissingTextureAtlasSprite)
            return 0;

        NativeImage[] mImage = ((MainImageAccessor)texture).getMainImage();
        NativeImage img = mImage.length == 0 ? null : mImage[0];
        if (img == null) return 0;
        int[] domColor = ColorThief.getColor(img);
        return domColor == null ? 0 : RGBUtil.packRGB(domColor);
    }


    /**
     * Cycles through a hue at a rate of 0.001f creating a rainbow effect. This automatically cycles on its own if ColorAPI.colorCycle is true.
     */
    public static void tick() {
        if (!colorCycle) return;
        hue += 0.001f;
        if (hue > 1f)
            hue -= 1f;
        color = HSBtoRGB(hue, 0.5f, 0.75f);
    }

    /**
     * A setter method for colorCycle. If enabled, automatically ticks through a hue at a slow rate, creating a rainbow effect.
     * @param colorCycle A boolean that enables or disables colorCycle.
     */
    public static void setColorCycle(boolean colorCycle) {
        ColorAPI.colorCycle = colorCycle;
    }
}
