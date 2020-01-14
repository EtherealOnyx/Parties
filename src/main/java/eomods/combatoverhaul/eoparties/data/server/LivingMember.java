package eomods.combatoverhaul.eoparties.data.server;

import net.minecraft.entity.player.PlayerEntity;

public class LivingMember {
    private PlayerEntity player;
    private String name;

    public LivingMember(PlayerEntity entity) {
        this.player = entity;
        this.name = entity.getName().getFormattedText();
    }

    public LivingMember(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void setPlayer(PlayerEntity entity) {
        player = entity;
    }
}
