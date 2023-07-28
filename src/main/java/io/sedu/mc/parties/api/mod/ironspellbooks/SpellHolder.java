package io.sedu.mc.parties.api.mod.ironspellbooks;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SpellHolder {

    public MutableComponent name;
    public ResourceLocation location;
    public CastType type;
    public School school;

    public enum CastType {
        NORMAL,
        HOLD,
        CHANNEL
    }

    public enum School {
        NONE( 0xffcc5b, 0xDDA528, 0x3F2B00, 0xFFDB8A),
        FIRE( 0xffcc5b, 0xDDA528, 0x3F2B00, 0xFFDB8A),
        ICE( 0xa6d1ff, 0xc5e3ff, 0x6a829f, 0xa8ccfb),
        LIGHTNING( 0x87f9fe, 0x77ccd2, 0x508689, 0xaffdff),
        HOLY( 0xfff8d4, 0xCABF87, 0x625926, 0xFFFDF2),
        ENDER( 0xfe95f0, 0xc872ae, 0x7a3872, 0xffaff0),
        BLOOD( 0xea504d, 0xa83c3a, 0x662628, 0xffa0a0),
        EVOCATION(0xFFFFFF, 0xBEBEBE, 0x4C4545, 0xFFFFFF),
        VOID( 0x793f91, 0x5f2573, 0x43005a,  0xcb89f1),
        POISON( 0x71fe76, 0x61d162, 0x559b5e, 0xbdffbf);

        public final int bgTop;
        public final int bgBottom;
        public final int bgInner;
        public final int bgTextColor;

        School(int bgTop, int bgBottom, int bgInner, int bgTextColor) {
            this.bgTop = bgTop;
            this.bgBottom = bgBottom;
            this.bgInner = bgInner;
            this.bgTextColor = bgTextColor;
        }


    }

    public SpellHolder(MutableComponent name, ResourceLocation location, CastType type, School school) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.school = school;
    }

    //enum SchoolType {}

}
