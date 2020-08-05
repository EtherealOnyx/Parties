package com.github.etherealonyx.parties.data;

import com.github.etherealonyx.parties.data.server.PacketHelper;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/*
 * This class stores any player data related on the server side. This also stores any pets that the current
 * player has.
 */
public class PlayerData {

    //List of Pet UUIDs that belong to the player.
    private HashSet<UUID> pets;
    //A reference to the party the player currently belongs to.
    private PartyData party;
    //A boolean that indicates whether or not this player is the party leader.
    private boolean partyLeader;
    //A boolean that indicates whether or not this player is online.
    private boolean isOnline;
    private PlayerEntity player;



    //'Default' constructor that stores the playerID this playerData belongs to.
    public PlayerData() {
        isOnline = true;
        party = null;
    }

    public PlayerData(UUID... pets) {
        this();
        addPets(pets);
    }

    public PlayerData(PlayerEntity player) {
        this.player = player;
        isOnline = true;
        party = null;
    }

    public boolean addPets(UUID... pets) {
        return this.pets.addAll(Arrays.asList(pets));
    }

    public boolean assignParty(PartyData party) {
        if (party == null)
            return false;
        else {
            this.party = party;
            return true;
        }
    }

    public PartyData getParty() {
        return party;
    }

    public boolean hasParty() {
        return party != null;
    }

    public void clearParty() {
        party = null;
        removeLead();
    }

    public boolean isLeader() {
        return partyLeader;
    }

    public void removeLead() {
        partyLeader = false;
    }

    public void makeLeader() {
        partyLeader = true;
    }

    public void markOnline() {
        System.out.println("Player is now online!");
        isOnline = true;
    }

    public void markOffline() {
        System.out.println("Player is now offline!");
        isOnline = false;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void assignEntity(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}
