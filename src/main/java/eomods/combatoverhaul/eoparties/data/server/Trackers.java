package eomods.combatoverhaul.eoparties.data.server;

import java.util.*;

import static eomods.combatoverhaul.eoparties.data.server.ServerData.*;
import static eomods.combatoverhaul.eoparties.data.server.Util.*;

public class Trackers {

    static HashMap<UUID, HashSet<UUID>> getClientTrackers() {
        return clientTrackers;
    }

    static void moveToServer(UUID toTrack) {
        if (clientTrackers.containsKey(toTrack)) {

            //This snip adds the trackers to the server-side trackers.
            //This also removes them from the client trackers, and sends the clients that information.
            moveTracker(toTrack, clientTrackers.get(toTrack));
        }
    }
    static void removeTracker(UUID toTrack, UUID clientTracker) {
        clientTrackers.get(toTrack).remove(clientTracker);
        Triggers.removeClientInfo(toTrack, clientTracker);

    }

    private static void moveTracker(UUID toTrack, HashSet<UUID> trackers) {
        for (UUID tracker : trackers) {
            Events.moveToServer(toTrack, tracker);
            Triggers.removeClientInfo(toTrack, tracker);
        }
    }

    static HashSet<UUID> getTrackers(UUID player) {
        return trackers.getOrDefault(player, EMPTY);
    }

    static void remove(UUID player) {
        //Remove all people tracking player.
        trackers.remove(player);
        clientTrackers.remove(player);
        for (Map.Entry<UUID, HashSet<UUID>> toTrackers : trackers.entrySet()) {
            //Found a player tracker.
            if (toTrackers.getValue().contains(player)) {
                //Remove player from entry.
                trackers.get(toTrackers.getKey()).remove(player);
                //If entry is now empty
                if (trackers.get(toTrackers.getKey()).size() == 0)
                    //Completely remove the tracker.
                    trackers.remove(toTrackers.getKey());
            }
        }

        for (Map.Entry<UUID, HashSet<UUID>> toTrackers : clientTrackers.entrySet()) {
            //Found a player tracker.
            if (toTrackers.getValue().contains(player)) {
                //Remove player from entry.
                clientTrackers.get(toTrackers.getKey()).remove(player);
                //If entry is now empty
                if (clientTrackers.get(toTrackers.getKey()).size() == 0)
                    //Completely remove the tracker.
                    clientTrackers.remove(toTrackers.getKey());
            }
        }
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

    public static void moveSelfToServer(UUID player) {
        if (clientTrackers.containsKey(player)) {
            if (trackers.containsKey(player))
                trackers.get(player).addAll(clientTrackers.get(player));
            else
                trackers.put(player, clientTrackers.get(player));
            clientTrackers.remove(player);
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
    private static void addNewTracker(UUID toTrack, UUID player) {
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

}
