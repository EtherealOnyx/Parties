package io.sedu.mc.parties.data;

import java.util.UUID;

import static io.sedu.mc.parties.data.Util.*;

public class PartyHelper {

    public static boolean invitePlayer(UUID initiator, UUID futureMember) {
        //This checks if futureMember is a valid player that exists on the server.
        if (getPlayer(futureMember) == null) {
            System.out.println("Target member wasn't a player!");
            return false;
        }
        //This checks if the target is currently in a party.
        if (getPlayer(futureMember).hasParty()) {
            System.out.println("Target player already has a party!");
            return false;
        }

        //This checks if initiator is in a party.
        if (!getPlayer(initiator).hasParty()) {
            new PartyData(initiator);
        }
        return addPlayerToParty(futureMember, getPartyFromMember(initiator));
    }

    //This adds the player to the given party.
    public static boolean addPlayerToParty(UUID futureMember, PartyData currentParty) {
        currentParty.addMember(futureMember);

        //TODO: Implement Packets.
        //Add all members to the party (Also sends the entire party to the futureMember).
        //PacketHelper.addMember(futureMember);
        //Tell futureMember who is the new leader.
        //PacketHelper.updateLeader(currentParty.getLeader(), futureMember);
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
        //PacketHelper.kickPlayer(removedMember);
        return removePlayerFromParty(removedMember);
    }

    public static boolean removePlayerFromParty(UUID removedMember) {
        //Remove player from the party.
        getPartyFromMember(removedMember).removeMember(removedMember);
        return true;
    }
}
