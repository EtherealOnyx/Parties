package eomods.combatoverhaul.eoparties.data.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
class RenderPetMember {
    private String name;
    public RenderPetMember() {
        name = null;
    }
    void setName(String name) {
        this.name = name;
    }

}
