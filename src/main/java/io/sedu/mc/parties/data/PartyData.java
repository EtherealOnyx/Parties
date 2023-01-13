package io.sedu.mc.parties.data;

import java.util.ArrayList;
import java.util.HashMap;
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
        leader = initiator;
        party = new ArrayList<>();
        party.add(initiator);

        //Generate a new party.
        partyId = UUID.randomUUID();
        PlayerData.playerList.get(initiator).addParty(partyId);
        partyList.put(partyId, this);
    }

    public void addMember(UUID futureMember) {
        //No checks necessary here as they have already been done.
        party.add(futureMember);
        Util.getPlayer(futureMember).addParty(partyId);

    }
}
