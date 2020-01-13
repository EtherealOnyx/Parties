package eomods.combatoverhaul.eoparties.data.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RenderPartyMember extends RenderMember {
    private boolean isOnline = false;
    private HashMap<UUID, RenderMember> petList = new HashMap<>();
    //Maybe support for future pets that have a foodLevel?
    private int foodLevel;

    RenderPartyMember(UUID... pets) {
        super();
        petList = new HashMap<>();
        for (UUID pet : pets) {
            petList.put(pet, new RenderMember());
        }
    }

    RenderPartyMember(String name) {
        super(name);
    }

    public boolean isOnline() {
        return isOnline;
    }

    void setOnline(boolean online) {
        isOnline = online;
    }

    void addPet(UUID... pets) {
        for (UUID pet : pets) {
            petList.put(pet, new RenderMember());
        }
    }

    void removePet(UUID... pets) {
        for (UUID pet : pets) {
            petList.remove(pet);
        }
    }

    Set<UUID> getPetList() {
        return petList.keySet();
    }

    HashMap<UUID, RenderMember> getPets() {
        return petList;
    }
    RenderMember getPetMember(UUID pet) {
        return petList.get(pet);
    }

    @Override
    int update() {
        //Player-only updates example
        if (((PlayerEntity) entity).getFoodStats().getFoodLevel() != foodLevel)
            return 1;
        //Checks all other common stats between pet/player, like health.
        return super.update();
    }

    @Override
    float getAmount(int updateType) {
        float amount = super.getAmount(updateType);
        return (amount == 0) ? getAmountPlayer(updateType) : amount;
    }

    float getAmountPlayer(int updateType) {
        switch(updateType) {
            case 10:
                //Food levels...
                return ((PlayerEntity) entity).getFoodStats().getFoodLevel();
        }
        return 0;
    }

}
