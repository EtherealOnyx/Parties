package io.sedu.mc.parties.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class PartyData {

    //Keeps track of all the parties currently on the server. May also be used to track the singular party on client.
    public static HashMap<UUID, PartyData> partyList = new HashMap<>();

    //The currently selected leader of the party.
    private UUID leader;

    //The list of players currently in the party.
    private ArrayList<UUID> party;

    private UUID partyId;

    //This party is created by the initiator.
    //They automatically get lead and get added to a party.
    public PartyData(UUID initiator) {
        party = new ArrayList<>();

        //Generate a new party.
        partyId = UUID.randomUUID();
        partyList.put(partyId, this);
        leader = initiator;
        addMember(initiator);
    }

    public void addMember(UUID futureMember) {
        //No checks necessary here as they have already been done.
        party.add(futureMember);
        Util.getPlayer(futureMember).addParty(partyId);
        //TODO: Implement Packets (send all party info not just lead)
        //PacketHelper.updateLeader(initiator);

    }

    public ArrayList<UUID> getMembers() {
        return party;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PartyData)
            return this.partyId == ((PartyData)o).partyId;
        return false;

    }

    public void removeMember(UUID removedMember) {
        if (!party.remove(removedMember))
            //Some error occured!
            System.out.println("This should never get here");
        Util.getPlayer(removedMember).removeParty();

        //Delete party if necessary.
        if (party.size() == 1) {
            System.out.println("Party disbanding!");
            disband();
            return;
            //PacketHelper.disbandParty(party);
        }

        //Update Leader
        if (removedMember.equals(leader)) {
            leader = party.get(0);
            //PacketHelper.updateLeader(leader, party);
        }
    }

    public void disband() {
        //To avoid concurrent modification....
        Iterator<UUID> i = party.iterator();
        while (i.hasNext()) {
            Util.getPlayer(i.next()).removeParty();
            i.remove();
        }
        partyList.remove(partyId);
    }
}
