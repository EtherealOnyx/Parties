package eomods.combatoverhaul.eoparties.data.client;

import eomods.combatoverhaul.eoparties.data.E_TYPE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RenderPartyMember extends RenderMember {
    private boolean isOnline = false;
    private HashMap<UUID, RenderMember> petList = new HashMap<>();
    //Maybe support for future pets that have a foodLevel?
    private int hunger;
    private int hungerTick;
    private float saturation;
    private float saturationTick;

    RenderPartyMember(UUID... pets) {
        super();
        petList = new HashMap<>();
        for (UUID pet : pets) {
            petList.put(pet, new RenderMember());
        }
    }

    RenderPartyMember(List<UUID> pets) {
        super();
        petList = new HashMap<>();
        for (UUID pet : pets) {
            petList.put(pet, new RenderMember());
        }
    }

    RenderPartyMember(String name) {
        super(name);
        isOnline = true;
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

    void addPet(List<UUID> pets) {
        for (UUID pet : pets) {
            petList.put(pet, new RenderMember());
        }
    }

    void removePet(UUID... pets) {
        for (UUID pet : pets) {
            petList.remove(pet);
        }
    }

    void removePet(List<UUID> pets) {
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

    boolean containsPet(UUID pet) {
        return petList.containsKey(pet);
    }

    @Override
    int update() {
        //Player-only updates example

        if (((PlayerEntity) entity).getFoodStats().getFoodLevel() != hunger)
            return E_TYPE.HUNGER.value();
        if (((PlayerEntity) entity).getFoodStats().getSaturationLevel() != saturation)
            return E_TYPE.SATURATION.value();
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
