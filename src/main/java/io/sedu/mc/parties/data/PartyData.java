package io.sedu.mc.parties.data;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.events.PartyJoinEvent;
import io.sedu.mc.parties.network.ServerPacketHelper;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static io.sedu.mc.parties.data.PlayerData.addTracker;
import static io.sedu.mc.parties.data.PlayerData.removeTracker;
import static io.sedu.mc.parties.data.ServerConfigData.partySize;

public class PartyData {

    //Keeps track of all the parties currently on the server. May also be used to track the singular party on client.
    public static HashMap<UUID, PartyData> partyList = new HashMap<>();

    //The currently selected leader of the party.
    private UUID leader;

    //The list of players currently in the party.
    private final ArrayList<UUID> party;

    private final UUID partyId;

    private int xpOverflow = 0;

    //This party is created by the initiator.
    //They automatically get lead and get added to a party.
    public PartyData(UUID initiator) {
        party = new ArrayList<>();

        partyId = UUID.randomUUID();
        partyList.put(partyId, this);

        party.add(initiator);
        //The player MUST exist here.
        Util.getPlayer(initiator, (playerData) -> playerData.addParty(partyId));
        updateLeaderNew(initiator);
    }

    public PartyData(UUID partyId, UUID leader, boolean addToParty) {
        party = new ArrayList<>();
        this.partyId = partyId;
        this.leader = leader;
        if (addToParty) {
            party.add(leader);
        }

    }

    private void updateLeaderNew(UUID initiator) {
        leader = initiator;
        ServerPacketHelper.sendNewLeader(initiator);
    }

    public void addMember(UUID futureMember) {
        //No checks necessary here as they have already been done.
        Parties.LOGGER.debug("Add Member internal START");
        //Add future member to party's trackers.
        party.forEach(id -> {
            //Add future member to id's trackers.
            addTracker(id, futureMember);
            //Add id to future member's trackers.
            addTracker(futureMember, id);
        });
        Parties.LOGGER.debug("Add Member internal POST-trackers");
        ServerPacketHelper.sendNewMember(futureMember, party);
        Parties.LOGGER.debug("Add Member internal POST-packet");
        party.add(futureMember);
        Parties.LOGGER.debug("Add Member internal POST-party");
        Util.getPlayer(futureMember, (playerData) -> playerData.addParty(partyId));
        Parties.LOGGER.debug("Add Member internal POST-player party info");
        //API Helper
        Util.getServerPlayer(futureMember, (serverPlayer) -> MinecraftForge.EVENT_BUS.post(new PartyJoinEvent(serverPlayer)));
        Parties.LOGGER.debug("Add Member internal POST-api");
    }

    public void addMemberSilently(UUID futureMember) {
        //Add future member to party's trackers.
        party.forEach(id -> {
            //Add future member to id's trackers.
            addTracker(id, futureMember);
            //Add id to future member's trackers.
            addTracker(futureMember, id);
        });

        party.add(futureMember);


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
            Parties.LOGGER.error("Error removing a party member from party!");
            //Some error occured!


        Util.getPlayer(removedMember, PlayerData::removeParty);
        ServerPacketHelper.sendRemoveMember(removedMember, party, wasKicked);
        //Remove previous member from party's trackers.
        party.forEach(id -> {
            //Remove member trackers for each id.
            removeTracker(removedMember, id);
            removeTracker(id, removedMember);
        });
        //Delete party if necessary.
        //Only if partySync is not enabled.
        if (party.size() == 1 && !ServerConfigData.isPartySyncEnabled()) {
            disband();
            return;
        }

        //Update Leader
        if (removedMember.equals(leader)) {
            updateLeader(party.get(0));
        }
    }

    public void disband() {
        if (party.size() >= 1) {
            Util.getServerPlayer(party.get(0), (player) -> {
                player.giveExperiencePoints(xpOverflow);
            });
        }
        ServerPacketHelper.disband(party);
        //To avoid concurrent modification....
        Iterator<UUID> i = party.iterator();
        while (i.hasNext()) {
            UUID member = i.next();
            Util.getPlayer(member, PlayerData::removeParty);
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

    public boolean isFull() {
        if (ServerConfigData.isPartySyncEnabled()) return false;
        return party.size() >= partySize.get();
    }

    public int getXpOverflow() {
        return xpOverflow;
    }

    public void setXpOverflow(int i) {
        xpOverflow = i;
    }
}
