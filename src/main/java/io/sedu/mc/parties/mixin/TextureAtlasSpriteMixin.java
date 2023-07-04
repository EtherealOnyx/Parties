package io.sedu.mc.parties.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import io.sedu.mc.parties.mixinaccessors.MainImageAccessor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin implements MainImageAccessor {

    @Shadow
    @Final
    public NativeImage[] mainImage;

    @Override
    public NativeImage[] getMainImage() {
        return mainImage;
    }

}
