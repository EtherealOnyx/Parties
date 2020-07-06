package com.github.etherealonyx.parties.data.server;

import com.github.etherealonyx.parties.data.PartyData;

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


    //'Default' constructor that stores the playerID this playerData belongs to.
    public PlayerData() {
        System.out.println("Adding new player!");
        isOnline = true;
        party = null;
    }

    public PlayerData(UUID... pets) {
        this();
        addPets(pets);
    }

    private boolean addPets(UUID... pets) {
        //TODO: Tell the client to add the pets as well.
        return this.pets.addAll(Arrays.asList(pets));
    }

    public boolean assignParty(PartyData party) {
        //TODO: Tell the client to add the party.
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
        //TODO: Tell the client that they are no longer in the party.
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
}
