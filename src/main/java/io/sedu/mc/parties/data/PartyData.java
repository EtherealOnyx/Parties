package io.sedu.mc.parties.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import io.sedu.mc.parties.network.ServerPacketHelper;

import static io.sedu.mc.parties.data.PlayerData.*;

public class PartyData {

    //Keeps track of all the parties currently on the server. May also be used to track the singular party on client.
    public static HashMap<UUID, PartyData> partyList = new HashMap<>();

    //The currently selected leader of the party.
    private UUID leader;

    //The list of players currently in the party.
    private final ArrayList<UUID> party;

    private final UUID partyId;

    //This party is created by the initiator.
    //They automatically get lead and get added to a party.
    public PartyData(UUID initiator) {
        party = new ArrayList<>();

        partyId = UUID.randomUUID();
        partyList.put(partyId, this);

        party.add(initiator);
        Util.getPlayer(initiator).addParty(partyId);
        updateLeaderNew(initiator);
    }

    private void updateLeaderNew(UUID initiator) {
        leader = initiator;
        ServerPacketHelper.sendNewLeader(initiator);
    }

    public void addMember(UUID futureMember) {
        //No checks necessary here as they have already been done.
        System.out.println("In addMember()...");
        //Add future member to party's trackers.
        party.forEach(id -> {
            //Add future member to id's trackers.
            addTracker(id, futureMember);
            //Add id to future member's trackers.
            addTracker(futureMember, id);
        });

        ServerPacketHelper.sendNewMember(futureMember, party);
        System.out.println("Packet sent successfully...");
        party.add(futureMember);
        System.out.println("Added member to party...");
        Util.getPlayer(futureMember).addParty(partyId);
        System.out.println("Added party id to member...");

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

    public void removeMember(UUID removedMember, boolean wasKicked) {
        if (!party.remove(removedMember))
            //Some error occured!
            System.out.println("This should never get here");
        Util.getPlayer(removedMember).removeParty();
        ServerPacketHelper.sendRemoveMember(removedMember, party, wasKicked);
        //Remove previous member from party's trackers.
        party.forEach(id -> {
            //Remove member trackers for each id.
            removeTracker(removedMember, id);
            removeTracker(id, removedMember);
        });
        //Delete party if necessary.
        if (party.size() == 1) {
            System.out.println("Party disbanding!");
            disband();
            return;
        }

        //Update Leader
        if (removedMember.equals(leader)) {
            updateLeader(party.get(0));
        }
    }

    public void disband() {
        ServerPacketHelper.disband(party);
        //To avoid concurrent modification....
        Iterator<UUID> i = party.iterator();
        while (i.hasNext()) {
            UUID member = i.next();
            Util.getPlayer(member).removeParty();
            PlayerData.playerTrackers.remove(member);
            i.remove();
        }
        partyList.remove(partyId);
    }

    public void updateLeader(UUID newLeader) {
        leader = newLeader;
        ServerPacketHelper.sendNewLeader(newLeader, party);
    }


    public boolean isLeader(UUID playerId) {
        return leader.equals(playerId);
    }

    public UUID getLeader() {
        return leader;
    }
}
