package eomods.combatoverhaul.eoparties.data.client;

import java.util.UUID;

public class StatUpdateRenderHelper {
    UUID ownerId;
    UUID entityId;
    int updateType;
    float updateAmount;
    float currentAmount;
    boolean isLess;

    public StatUpdateRenderHelper(UUID owner, UUID entityToUpdate, int type, float amount) {
        this.ownerId = owner;
        this.entityId = entityToUpdate;
        this.updateType = type;
        this.updateAmount = amount;
    }
}
