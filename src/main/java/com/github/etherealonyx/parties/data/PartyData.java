package com.github.etherealonyx.parties.data;

import com.github.etherealonyx.parties.data.client.ClientData;

import java.util.ArrayList;
import java.util.UUID;

public class PartyData {

    private UUID partyLeader;
    private ArrayList<UUID> partyMembers;

    public PartyData(UUID partyLeader) {
        System.out.println("Creating new party!");
        this.partyLeader = partyLeader;
        partyMembers = new ArrayList<>();
        addMember(partyLeader);
    }

    public PartyData() {
        System.out.println("Creating new party!");
        partyMembers = new ArrayList<>();
        partyMembers.add(ClientData.client);
    }

    public boolean addMember(UUID partyMember) {
        System.out.println("Adding party members!");
        return this.partyMembers.add(partyMember);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        return (o instanceof PartyData && ((PartyData) o).partyMembers == this.partyMembers);
    }

    @Override
    public int hashCode()
    {
        return partyLeader.hashCode();
    }

    public UUID getLeader() {
        return partyLeader;
    }

    public void changeLeader(UUID newLead) {
        partyLeader = newLead;
        //TODO: Tell all party member's clients to change the leader.
    }
    public void removeMember(UUID removedMember) {
        System.out.println("Removing party member!");
        this.partyMembers.remove(removedMember);
        //TODO: Tell all party member's clients to remove that member.
    }

    public int getSize() {
        return partyMembers.size();
    }

    public ArrayList<UUID> getMembers() {
        return partyMembers;
    }

    public UUID getMember(int i) {
        return partyMembers.get(i);
    }
}
