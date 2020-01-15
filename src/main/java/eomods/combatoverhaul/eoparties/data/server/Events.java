package eomods.combatoverhaul.eoparties.data.server;

import eomods.combatoverhaul.eoparties.config.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.HashSet;
import java.util.UUID;

import static eomods.combatoverhaul.eoparties.data.server.ServerData.*;
import static eomods.combatoverhaul.eoparties.data.server.Util.*;

//Events cover anything that is called whenever a certain event happens - like when the player needs to join a party,
// when they go offline, etc.
public class Events {
    //This attempts to add a player to a party. There's only two players here - the requester and requested.

    private static int getIndex(UUID partyMember) {
        for (int i = 0; i < parties.size(); i++) {
            if (parties.get(i).contains(partyMember)) {
                return i;
            }
        }

        return -1;
    }
    public static boolean addPlayerToParty(UUID inviter, UUID invited) {
        boolean inviterInParty = false;
        boolean invitedInParty = false;
        int index = -1;
        for (int i = 0; i < parties.size(); i++) {
            if (parties.get(i).contains(inviter)) {
                inviterInParty = true;
                index = i;
            }
            if (parties.get(i).contains(invited)) {
                invitedInParty = true;
                index = i;
            }
        }
        if (inviterInParty) {
            if (invitedInParty)
                return false;
            //Party List in index belongs to inviter.
            return addPlayerToParty(invited, index);
        } else {
            if (invitedInParty) {
                //Party list on index belongs to invited.
                return addPlayerToParty(invited, index);
            } else {
                //No player has a party.
                createNewParty(inviter, invited);
                return true;
            }
        }
    }
    private static void createNewParty(UUID inviter, UUID invited) {
        //Store party.
        HashSet<UUID> freshParty = new HashSet<>();
        freshParty.add(inviter);
        parties.add(freshParty);
        //Add a party leader.
        partyLeaders.add(inviter);
        //Tells the clients of their party leader.
        Triggers.updateLeader(inviter);
        addPlayerToParty(invited, parties.size()-1);
    }

    private static boolean addPlayerToParty(UUID invited, int index) {
        if (parties.get(index).size() > Config.MAX_PARTY_SIZE - 1)
            return false;
        //Add player to party.
        parties.get(index).add(invited);

        //Update information for player.
        updatePartyInfo(invited, index, true);
        return true;
    }

    private static void updatePartyInfo(UUID playerJoiningOrInvited, int index, boolean isNew) {
        //Send player all UUIDs of their existing party members, including self.
        Triggers.updatePartyMember(playerJoiningOrInvited, parties.get(index), isNew);

        //Update player trackers.
        if (isNew)
            Trackers.addNewTracker(playerJoiningOrInvited, parties.get(index));
        else {
            //Try to add new trackers...?
            Trackers.attemptAddNewTracker(playerJoiningOrInvited, parties.get(index));
            Trackers.addPetTrackers(playerJoiningOrInvited);
        }



        //Send partyMember and joiner name information.
        Triggers.updateNameJoin(playerJoiningOrInvited);
        if (isNew)
            Triggers.updateName(playerJoiningOrInvited);

        //Send player leader information.
        Triggers.sendLeader(playerJoiningOrInvited, parties.get(index));

        //Send all online information.
        Triggers.updateOnline(playerJoiningOrInvited, parties.get(index));

        if (isNew)
            Triggers.sendClientRefresh(getParty(playerJoiningOrInvited));
        else
            Triggers.sendClientRefresh(playerJoiningOrInvited);
    }

    public static void onPlayerJoin(ServerPlayerEntity player) {
        Triggers.markOnline(player);
        int index = getIndex(player.getUniqueID());
        if (index != -1) {
            updatePartyInfo(player.getUniqueID(), index, false);
        } else {
            Trackers.addPetTrackers(player.getUniqueID());
            Triggers.updatePetNames(player.getUniqueID());
            Triggers.sendClientRefresh(player.getUniqueID());
        }
    }



    public static void onPlayerLeave(UUID player) {
        //Mark player as offline, send packet to other trackers indicating player is offline.
        Triggers.markOffline(player);
        checkLeader(player);
        //Remove player from all trackers. They don't need to be tracked while offline (they can't).
        Trackers.removeOnlyPlayerAll(player);
    }

    public static void checkLeader(UUID player) {
        if (partyLeaders.contains(player)) {
            if (Triggers.nextLeader(getParty(player)))
                partyLeaders.remove(player);
        }
    }

    public static void moveAllToServer(UUID playerOrPet) {
        //This checks if that entity has any trackers, and then tells the trackers to move them to server side tracking.
        Trackers.moveToServer(playerOrPet, false);

        //This tells the player to move all client trackers to server trackers, if they are a player.
        if (Util.hasParty(playerOrPet) || Util.hasSubParty(playerOrPet)) {
            Trackers.moveAllToServer(playerOrPet);
        }
    }

    public static void moveToClient(UUID playerTracker, UUID toTrack) {
        Trackers.moveToClient(playerTracker, toTrack);
    }

    public static void moveToServer(UUID playerTracker, UUID toTrack) {
        Trackers.moveToServer(playerTracker, toTrack);
    }

    public static boolean validatePartyMember(UUID requestingPlayer, UUID toTrack) {
        if (getSubParty(requestingPlayer).contains(toTrack))
            return true;
        for (UUID partyMember : getParty(requestingPlayer)) {
            //Checks if toTrack is a party member, or one of party member's pets.
            if (partyMember.equals(toTrack) || subParties.getOrDefault(partyMember, EMPTY).contains(toTrack))
                return true;
        }
        return false;
    }


    public static boolean dropParty(UUID droppingPlayer) {
        return dropParty(droppingPlayer, getParty(droppingPlayer));
    }

    public static boolean dropParty(UUID droppingPlayer, HashSet<UUID> party) {
        if (party.size() == 0)
            return false;
        party.remove(droppingPlayer);
        Triggers.removeParty(droppingPlayer);
        Triggers.removeMemberFromParty(droppingPlayer, party);
        if (party.size() == 1) {
            Triggers.disbandParty(party.iterator().next());
            partyLeaders.remove(droppingPlayer);
            parties.remove(party);
            party.clear();
            return true;
        }
        //Check leaders...
        checkLeader(droppingPlayer);
        return true;
    }

    public static boolean kickPartyMember(UUID playerToKick) {
        return kickPartyMember(playerToKick, getParty(playerToKick));
    }

    public static boolean kickPartyMember(UUID playerToKick, HashSet<UUID> party) {
        if (party.size() == 0)
            return false;
        party.remove(playerToKick);
        Triggers.removePartyKicked(playerToKick);
        Triggers.removeMemberFromPartyKicked(playerToKick, party);
        if (party.size() == 1) {
            Triggers.disbandParty(party.iterator().next());
            partyLeaders.remove(playerToKick);
            parties.remove(party);
            party.clear();
            return true;
        }
        //Check leaders...
        checkLeader(playerToKick);
        return true;
    }

    public static boolean addPetToParty(UUID owner, LivingEntity pet) {
        HashSet<UUID> party = getSubParty(owner);
        if (party.size() >= Config.MAX_SUBPARTY_SIZE || party.contains(pet.getUniqueID()))
            return false;
        livingMembers.put(pet.getUniqueID(), new LivingMember(pet.getName().getFormattedText()));
        //Add the subParty member.
        if (party.size() == 0) {
            party = new HashSet<>();
            party.add(pet.getUniqueID());
            subParties.put(owner, party);
            System.out.println("Placing " + getName(owner) + " in a new subparty.");
        } else {
            subParties.get(owner).add(pet.getUniqueID());
        }
        //Add all party members to tracker, if they exist, and send pet info to clients.
        if (hasParty(owner)) {
            HashSet<UUID> party2 = getParty(owner);
            Trackers.addNewPetTracker(pet.getUniqueID(), party2);
            Triggers.addPet(pet.getUniqueID(), party2, owner);

            //Sends pet name info to all trackers members.
            Triggers.updateName(pet.getUniqueID());
            Triggers.sendClientRefresh(party2);
        }
        else {
            Trackers.addNewTracker(pet.getUniqueID(), owner);
            Triggers.updatePartyMember(owner, pet.getUniqueID(), owner);

            //Sends pet name info to all trackers members.
            Triggers.updateName(pet.getUniqueID());
            Triggers.sendClientRefresh(owner);
        }
        return true;
    }

    public static boolean kickPetMember(UUID owner, UUID pet) {
        if (!subParties.containsKey(owner))
            return false;
        if (!subParties.get(owner).contains(pet))
            return false;

        //Remove every tracker associated with the pet getting removed.
        Trackers.removeAllTrackers(pet);

        //Remove the pet from the subParty.
        subParties.get(owner).remove(pet);
        if (subParties.get(owner).size() == 0)
            subParties.remove(owner);

        //Tell clients to remove the pet from party.
        Triggers.removePetFromParty(owner, pet);

        return true;
    }
}
