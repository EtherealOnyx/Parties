package eomods.combatoverhaul.eoparties.data.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ServerData {

    //A list of all parties on the server.
    static ArrayList<HashSet<UUID>> parties = new ArrayList<>();

    //A list of sub-parties (owner and pets) on the server.
    static HashMap<UUID, HashSet<UUID>> subParties = new HashMap<>();

    //A set of all party leaders on the server.
    public static HashSet<UUID> partyLeaders = new HashSet<>();

    //A map of all players/entities.
    public static HashMap<UUID, LivingMember> livingMembers = new HashMap<>();

    //A map of trackers that need to be sent to clients.
    public static HashMap<UUID, HashSet<UUID>> trackers = new HashMap<>();

    //A map of trackers that are being updated on the client.
    public static HashMap<UUID, HashSet<UUID>> clientTrackers = new HashMap<>();
}
