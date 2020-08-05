package com.github.etherealonyx.parties.data.server;

import com.github.etherealonyx.parties.data.PartyData;

import java.util.UUID;

import static com.github.etherealonyx.parties.data.server.Util.*;

public class PartyHelper {



    public static boolean invitePlayer(UUID initiator, UUID futureMember) {
        //This checks if futureMember is a valid player that exists on the server.
        if (getMember(futureMember) == null) {
            System.out.println("Target member wasn't a player!");
            return false;
        }
        //This checks if the target is currently in a party.
        if (getMember(futureMember).hasParty()) {
            System.out.println("Target player already has a party!");
            return false;
        }

        //This checks if initiator is in a party.
        if (getParty(initiator) == null) {
            PartyData data = new PartyData(initiator);
            getMember(initiator).assignParty(data);
        }
        getMember(initiator).makeLeader();
        PacketHelper.updateLeader(initiator);
        return addPlayer(futureMember, getParty(initiator));
    }

    //This adds the player to the given party.
    public static boolean addPlayer(UUID futureMember, PartyData currentParty) {
        currentParty.addMember(futureMember);
        getMember(futureMember).assignParty(currentParty);

        //Add all members to the party (Also sends the entire party to the futureMember).
        PacketHelper.addMember(futureMember);
        //Tell futureMember who is the new leader.
        PacketHelper.updateLeader(currentParty.getLeader(), futureMember);
        //TODO: Add players to trackers. Also tell client who's online, who's lead, etc etc. Basically all party info.
        return true;
    }

    //Kicks player from party.
    public static boolean kickPlayer(UUID initiator, UUID removedMember) {
        if (!inSameParty(initiator, removedMember)) {
            System.out.println("Target is not in your party!");
            return false;
        }
        //Removes member.
        PacketHelper.kickPlayer(removedMember);
        return removePlayer(removedMember);
    }

    public static boolean removePlayer(UUID removedMember) {
        return removePlayer(removedMember, getParty(removedMember));
    }

    public static boolean removePlayer(UUID removedMember, PartyData party) {
        //Remove player from the party.
        party.removeMember(removedMember);
        //Remove the party reference from the player, and also removes lead if they had it in their PlayerData.
        getMember(removedMember).clearParty();

        //TODO: Remove removedMember's trackers and vice versa.

        //Delete party if necessary.
        if (party.getSize() == 1) {
            System.out.println("Party disbanding!");
            getMember(party.getMembers().get(0)).clearParty();
            PacketHelper.disbandParty(party);
            //No trackers needed to be removed here because they would've been removed before.
            return true;
        }
        //Checks if removed member was a leader.
        if (party.getLeader().equals(removedMember)) {
            //Updates the party itself with the new leader. Returns whether or not the leader change was successful.
            if (!updateLeader(party)) {
                //Forces the first person in the party list to become leader.
                updateLeader(party, party.getMember(0));
            }
        }
        return true;

    }

    static void updateLeader(PartyData party, UUID member) {
        System.out.println("Changing leader!!!");
        //Remove old leader from player data.
        getMember(party.getLeader()).removeLead();
        //Update leader in the party data.
        party.changeLeader(member);
        //Update leader in player data.
        getMember(member).makeLeader();
        PacketHelper.updateLeader(member);
    }

    static boolean updateLeader(PartyData party) {
        System.out.println("Trying to update leader...");
        for (UUID member : party.getMembers()) {
            System.out.println("Member: " + member.toString() + " = Online | " + getMember(member).isOnline());
            if (getMember(member).isOnline()) {
                updateLeader(party, member);
                return true;
            }
        }
        return false;

    }
}
