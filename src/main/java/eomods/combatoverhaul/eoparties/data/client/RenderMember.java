package eomods.combatoverhaul.eoparties.data.client;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.ChunkPos;

public class RenderMember {
    private String name;
    LivingEntity entity;
    private int health;

    public RenderMember() {
        name = null;
        entity = null;
        health = 0;
    }

    public RenderMember(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //This method will run when the player/entity is tracked client side.
    //The returned integer says what type to update.
    int update() {
        if (entity == null)
            return -1;
        //Example of future use.
        if (entity.getHealth() != health)
            return 0;
        return -1;
    }

    float getAmount(int upgradeType) {
        switch(upgradeType) {
            case 0:
                //Health.
                return entity.getHealth();
            case 1:
                //Max Health.
                return entity.getMaxHealth();
                //etc...
        }
        return 0;
    }

    ChunkPos getChunk() {
        return new ChunkPos(entity.chunkCoordX, entity.chunkCoordZ);
    }
}
