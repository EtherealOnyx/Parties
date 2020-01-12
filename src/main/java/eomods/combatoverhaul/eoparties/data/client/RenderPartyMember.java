package eomods.combatoverhaul.eoparties.data.client;

import net.minecraft.entity.EntityLivingBase;

import java.util.*;

public class RenderPartyMember {
    private String name;
    private boolean isOnline;
    private HashMap<UUID, RenderPetMember> petList;

    RenderPartyMember() {
        name = null;
        isOnline = false;
        petList = new HashMap<>();
    }

    RenderPartyMember(UUID... pets) {
        name = null;
        isOnline = false;
        petList = new HashMap<>();
        for (UUID pet : pets) {
            petList.put(pet, new RenderPetMember());
        }
    }

    RenderPartyMember(String name) {
        this.name = name;
        isOnline = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return isOnline;
    }

    void setOnline(boolean online) {
        isOnline = online;
    }

    void addPet(UUID... pets) {
        for (UUID pet : pets) {
            petList.put(pet, new RenderPetMember());
        }
    }

    void removePet(UUID... pets) {
        for (UUID pet : pets) {
            petList.remove(pet);
        }
    }

    HashMap<UUID, RenderPetMember> getPets() {
        return petList;
    }

    Set<UUID> getPetList() {
        return petList.keySet();
    }
}
