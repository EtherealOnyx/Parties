package io.sedu.mc.parties.api.helper;

import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.ServerPlayerData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PartyAPI {

    /**
     * Tries to get the PartyData that the provided player belongs to.
     * @param playerId The UUID of the player that is part of a party.
     * @return The PartyData instance that the player belongs to, or null if they don't have a party.
     */
    public static @Nullable PartyData getPartyFromMember(UUID playerId) {
        UUID party;
        ServerPlayerData pl;
        PartyData p;
        if ((pl = ServerPlayerData.playerList.get(playerId)) != null && (party = pl.getPartyId()) != null && (p = getPartyFromId(party)) != null)
            return p;
        return null;
    }

    /**
     * Tries to get the PartyData that the provided entity belongs to.
     * @param entity The LivingEntity object of the member to check for.
     * @return The PartyData instance that the player or owned entity belongs to.
     */
    public static PartyData getPartyFromMember(LivingEntity entity) {

        if (entity instanceof Player p) {
            return getPartyFromMember(p.getUUID());
        }
        //Entity is not a player.
        if (entity instanceof OwnableEntity ownedEntity) {
            //Check if owner entity is a player.
            if (ownedEntity.getOwner() instanceof Player p) {
                return getPartyFromMember(p.getUUID());
            }
        }
        return null;
    }

    /**
     * Provides a list of player objects that are part of the same party as the provided player.
     * @param memberId The player UUID that is part of the party and ignored from the returned list.
     * @return A List of Player objects that are online and part of the same party as the provided member.
     */
    public static List<Player> getOnlineMembersWithoutSelf(UUID memberId) {
        ArrayList<Player> players = new ArrayList<>();
        PartyData p;
        if ((p = getPartyFromMember(memberId)) != null) {
            for (UUID member : p.getMembers()) {
                if (!member.equals(memberId)) {
                    PlayerAPI.getServerPlayer(member, players::add);
                }
            }
        }
        return players;
    }

    /**
     * Provides a list of online AND nearby player objects that are part of the same party as the provided player.
     * @param memberId The player UUID that is part of the party and ignored from the returned list.
     * @return A List of Player objects that are online, nearby, and part of the same party as the provided member.
     */
    public static ArrayList<Player> getNearMembersWithoutSelf(UUID memberId) {
        ArrayList<Player> players = new ArrayList<>();
        PartyData p;
        if ((p = getPartyFromMember(memberId)) != null) {
            for (UUID member : p.getMembers()) {

                //not self check && is online check && is in range check
                if (!member.equals(memberId) && isClientTracked(memberId, member)) {
                    PlayerAPI.getServerPlayer(member, players::add);
                }
            }
        }
        return players;
    }


    /**
     * Checks if the provided players are part of the same party.
     * @param member1 The UUID of the first player.
     * @param member2 The UUID of the second player.
     * @return - A boolean that is true when the players are in the same party and false otherwise.
     */
    public static boolean inSameParty(UUID member1, UUID member2) {
        PartyData p1, p2;
        if ((p1 = getPartyFromMember(member1)) == null || (p2 = getPartyFromMember(member2)) == null)
            return false;
        return p1.equals(p2);
    }

    /**
     * Checks if the provided entities are part of the same party. This includes players and owned entities.
     * @param member1 The LivingEntity object holding the first member.
     * @param member2 The LivingEntity object holding the second object.
     * @return - A boolean that is true when the members are in the same party and false otherwise.
     */
    public static boolean inSameParty(LivingEntity member1, LivingEntity member2) {
        //Note that member1 and member2 is considered to be in the same party if one entity owns the other.
        //Check if member1 is a player and member2 is an ownable entity owned by member1.
        if (member1 instanceof Player p1 && member2 instanceof OwnableEntity own1 && p1.getUUID().equals(own1.getOwnerUUID())) {
            return true;
        }

        //Check if member2 is a player and member1 is an ownable entity owned by member2.
        if (member2 instanceof Player p1 && member1 instanceof OwnableEntity own1 && p1.getUUID().equals(own1.getOwnerUUID())) {
            return true;
        }

        PartyData p1, p2;
        if ((p1 = getPartyFromMember(member1)) == null || (p2 = getPartyFromMember(member2)) == null) {
            return false;
        }


        return p1.equals(p2);
    }




    /**
     * Tries to get the PartyData from the provided party UUID.
     * @param partyId The UUID of the party to search for.
     * @return The PartyData instance with the given party UUID, or null if the UUID doesn't exist.
     */
    public static PartyData getPartyFromId(UUID partyId) {
        return PartyData.partyList.get(partyId);
    }


    /**
     * Checks if the provided player is a party leader.
     * @param playerId The UUID of the player to check for.
     * @return A boolean that is true if the player is a leader, false if they are not.
     */
    public static boolean isLeader(UUID playerId) {
        PartyData p;
        if ((p = getPartyFromMember(playerId)) != null) {
            return p.isLeader(playerId);
        }
        return false;
    }

    /**
     * Checks if the provided player is part of a party.
     * @param playerId The UUID of the player to check for.
     * @return A boolean that is true if the player has a party, false if they do not.
     */
    public static boolean hasParty(UUID playerId) {
        return getPartyFromMember(playerId) != null;
    }

    //Internal help methods.
    private static boolean isClientTracked(UUID tracker, UUID toTrack) {
        HashMap<UUID, Boolean> trackers;
        Boolean serverTracked;
        if ((trackers = ServerPlayerData.playerTrackers.get(toTrack)) != null && (serverTracked = trackers.get(tracker)) != null)
            return !serverTracked;
        return false;
    }
}
