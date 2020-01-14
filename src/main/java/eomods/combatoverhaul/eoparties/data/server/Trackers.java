package eomods.combatoverhaul.eoparties.data.server;

import java.util.*;

import static eomods.combatoverhaul.eoparties.data.server.ServerData.*;
import static eomods.combatoverhaul.eoparties.data.server.Util.*;

public class Trackers {

    static HashMap<UUID, HashSet<UUID>> getClientTrackers() {
        return clientTrackers;
    }

    static void moveToServer(UUID toTrackOrBeTracked, boolean isTracker) {
        if (isTracker) {
            HashSet<UUID> toTracks = new HashSet<>();
            for (Map.Entry<UUID, HashSet<UUID>> trackers : clientTrackers.entrySet()) {
                //If the player is listed in something's clientTracker list...
                if (trackers.getValue().contains(toTrackOrBeTracked)) {
                    //Add them to our hash set up top
                    toTracks.add(trackers.getKey());
                    //Remove them from the clientTracker list.
                    trackers.getValue().remove(toTrackOrBeTracked);
                    //If the list is now empty...
                    if (trackers.getValue().size() == 0)
                        //Remove the toTrack completely from the clientTrackers.
                        clientTrackers.remove(trackers.getKey());
                }
            }
            if (toTracks.size() == 0)
                return;
            for (UUID toTrack : toTracks) {
                if (trackers.containsKey(toTrack)) {
                    trackers.get(toTrack).add(toTrackOrBeTracked);
                } else {
                    HashSet<UUID> set = new HashSet<>();
                    set.add(toTrackOrBeTracked);
                    trackers.put(toTrack, set);
                }
            }
        } else {
            if (clientTrackers.containsKey(toTrackOrBeTracked)) {

                //This snip adds the trackers to the server-side trackers.
                //This also removes them from the client trackers, and sends the clients that information.
                moveTracker(toTrackOrBeTracked, clientTrackers.get(toTrackOrBeTracked));
            }
        }

    }

    private static void moveTracker(UUID toTrack, HashSet<UUID> trackers) {
        for (UUID tracker : trackers) {
            Events.moveToServer(toTrack, tracker);
            Triggers.removeClientTracker(toTrack, tracker);
        }
    }

    static HashSet<UUID> getTrackers(UUID player) {
        return trackers.getOrDefault(player, EMPTY);
    }

    static void moveToClient(UUID playerTracker, UUID toTrack) {
        System.out.println("Removing " + getName(toTrack) + " from server...");
        if (trackers.containsKey(toTrack)) {
            trackers.get(toTrack).remove(playerTracker);
            if(trackers.get(toTrack).size() == 0)
                trackers.remove(toTrack);
        }

        if (clientTrackers.containsKey(toTrack))
            clientTrackers.get(toTrack).add(playerTracker);
        else {
            HashSet<UUID> set = new HashSet<>();
            set.add(playerTracker);
            clientTrackers.put(toTrack, set);
        }

    }
    static void moveToServer(UUID playerTracker, UUID toTrack) {
        if (trackers.containsKey(toTrack))
            trackers.get(toTrack).add(playerTracker);
        else {
            HashSet<UUID> set = new HashSet<>();
            set.remove(playerTracker);
            trackers.put(toTrack, set);
        }
        if (clientTrackers.containsKey(toTrack)) {
            clientTrackers.get(toTrack).remove(playerTracker);
            if(clientTrackers.get(toTrack).size() == 0)
                clientTrackers.remove(toTrack);
        }
    }

    public static void attemptAddNewTracker(UUID joiningPlayer, HashSet<UUID> party) {
        //Add joiningPlayer to party member's trackers.
        for (UUID partyMember : listWithoutSelf(party, joiningPlayer)) {
            //For all party members, check if they are tracking player in clientTracker.
            if (!clientTrackers.getOrDefault(joiningPlayer, EMPTY).contains(partyMember))
                //If party member doesn't have them in clientTrackers, then add them to server tracker.
                addNewTracker(joiningPlayer, partyMember);
        }

        //Add partyMembers to joiningPlayer's trackers.
        addNewTracker(listWithoutSelf(party, joiningPlayer), joiningPlayer);

    }
    public static void addNewPetTracker(UUID pet, HashSet<UUID> party) {
        HashSet<UUID> onlineMembers = new HashSet<>();
        for (UUID partyMember : party) {
            if (isOnline(partyMember))
                onlineMembers.add(partyMember);
        }
        trackers.put(pet, onlineMembers);
    }

    static void addNewTracker(UUID toTrack, List<UUID> trackers) {
        for (UUID tracker : trackers) {
            if (isOnline(toTrack))
                addNewTracker(toTrack, tracker);
            addNewTracker(getSubParty(toTrack), tracker);
        }
    }
    static void addNewTracker(List<UUID> toTracks, UUID tracker) {
        for (UUID toTrack : toTracks) {
            if (isOnline(toTrack))
                addNewTracker(toTrack, tracker);
            addNewTracker(getSubParty(toTrack), tracker);
        }
    }

    static void addNewTracker(HashSet<UUID> toTracks, UUID tracker) {
        for (UUID toTrack : toTracks)
            addNewTracker(toTrack, tracker);

    }
    static void addNewTracker(UUID toTrack, HashSet<UUID> trackers) {
        //Add party members to joining player's tracker info. Also adds party members to joining player's pets.
        addNewTracker(toTrack, Util.listWithoutSelf(trackers, toTrack));
        //Add party members and their pets.
        addNewTracker(Util.listWithoutSelf(trackers, toTrack), toTrack);
    }

    static void addNewTracker(UUID toTrack, UUID player) {
        if (isOnline(player)) {
            if (trackers.containsKey(toTrack))
                trackers.get(toTrack).add(player);
            else {
                HashSet<UUID> set = new HashSet<>();
                set.add(player);
                trackers.put(toTrack, set);
            }
        }
    }

    static void removeOnlyPlayerAll(UUID playerToRemove) {
        //Remove all people tracking player.
        removeAllTrackers(playerToRemove);
        //Remove player from all trackers;
        removeFromAllTrackers(playerToRemove);
    }

    private static void removeFromAllTrackers(UUID playerToRemove) {
        removeFromClientTrackers(playerToRemove);
        removeFromServerTrackers(playerToRemove);
    }

    private static void removeFromServerTrackers(UUID playerToRemove) {
        Iterator iter = trackers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, HashSet<UUID>> entry = (Map.Entry<UUID, HashSet<UUID>>) iter.next();
            if (entry.getValue().contains(playerToRemove)) {
                entry.getValue().remove(playerToRemove);
                if (entry.getValue().size() == 0)
                    iter.remove();
            }
        }
    }

    private static void removeFromClientTrackers(UUID playerToRemove) {
        Iterator iter = clientTrackers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, HashSet<UUID>> entry = (Map.Entry<UUID, HashSet<UUID>>) iter.next();
            if (entry.getValue().contains(playerToRemove)) {
                entry.getValue().remove(playerToRemove);
                if (entry.getValue().size() == 0)
                    iter.remove();
            }
        }
    }

    static void removeTracker(UUID toTrack, UUID tracker) {
        removeTrackerClient(toTrack, tracker);
        removeTrackerServer(toTrack, tracker);
    }

    static void removeTrackerClient(UUID toTrack, UUID tracker) {
        clientTrackers.getOrDefault(toTrack, EMPTY).remove(tracker);
        if (clientTrackers.getOrDefault(toTrack, EMPTY).size() == 0)
            clientTrackers.remove(toTrack);
    }

    static void removeTrackerServer(UUID toTrack, UUID tracker) {
        trackers.getOrDefault(toTrack, EMPTY).remove(tracker);
        if (trackers.getOrDefault(toTrack, EMPTY).size() == 0)
            trackers.remove(toTrack);

    }

    static void removeAllTrackers(UUID toTrack) {
        trackers.remove(toTrack);
        clientTrackers.remove(toTrack);
    }

    static void removeParty(UUID droppingPlayer) {
        //Remove all people tracking player.
        removeAllTrackers(droppingPlayer);
        //Remove all trackers from player's pets except self.
        removeAllPetTrackers(droppingPlayer);
    }

    private static void removeAllPetTrackers(UUID droppingPlayer) {
        for (UUID pet : getSubParty(droppingPlayer)) {
            removeAllTrackersExcept(pet, droppingPlayer);
        }
    }

    private static void removeAllTrackersExcept(UUID toTrack, UUID except) {
        HashSet<UUID> set = new HashSet<>();
        set.add(except);
        if (clientTrackers.get(toTrack).contains(except))
            clientTrackers.put(toTrack, set);
        else
            clientTrackers.remove(except);
        if (trackers.get(toTrack).contains(except))
            trackers.put(toTrack, set);
        else
            trackers.remove(except);
    }

    public static void removeMemberFromParty(UUID droppingPlayer, UUID partyMember) {
        //droppingPlayer already has no trackers. droppingPlayer's pets also have no trackers (aside from owner).

        //Remove droppingPlayer from partyMember's trackers.
        removeTracker(partyMember, droppingPlayer);
        //Remove droppingPlayer from partyMember's pets.
        removePetTracker(getSubParty(partyMember), droppingPlayer);
    }

    static void removePetTracker(HashSet<UUID> pets, UUID tracker) {
        for (UUID pet : pets)
            removeTracker(pet, tracker);
    }

    public static void moveAllToServer(UUID playerOrPet) {
        Trackers.moveToServer(playerOrPet, true);
        Triggers.moveAllToServer(playerOrPet);
    }

    public static void addPetTrackers(UUID owner) {
        if (getSubParty(owner).size() > 0) {
            Triggers.sendPetInfo(owner);
            for (UUID pet : getSubParty(owner)) {
                addNewTracker(pet, owner);
            }
        }



    }
}
