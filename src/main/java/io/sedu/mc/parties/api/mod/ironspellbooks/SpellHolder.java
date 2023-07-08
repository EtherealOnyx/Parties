package io.sedu.mc.parties.api.mod.ironspellbooks;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SpellHolder {

    public MutableComponent name;
    public ResourceLocation location;
    public CastType type;

    public enum CastType {
        NORMAL,
        HOLD,
        CHANNEL
    }

    public SpellHolder(MutableComponent name, ResourceLocation location, CastType type) {
        this.name = name;
        this.location = location;
        this.type = type;
    }

    //enum SchoolType {}

}
