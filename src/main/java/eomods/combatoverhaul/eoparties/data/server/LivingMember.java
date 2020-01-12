package eomods.combatoverhaul.eoparties.data.server;

import net.minecraft.entity.player.EntityPlayerMP;

public class LivingMember {
    private EntityPlayerMP player;
    private String name;

    public LivingMember(EntityPlayerMP entity) {
        this.player = entity;
        this.name = entity.getName();
    }

    public String getName() {
        return this.name;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }

    public void setPlayer(EntityPlayerMP entity) {
        player = entity;
    }
}
