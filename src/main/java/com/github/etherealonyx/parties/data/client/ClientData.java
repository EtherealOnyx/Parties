package com.github.etherealonyx.parties.data.client;

import com.github.etherealonyx.parties.data.PartyData;
import com.github.etherealonyx.parties.data.PlayerData;
import com.github.etherealonyx.parties.data.TrackerData;

import java.util.HashMap;
import java.util.UUID;

public class ClientData {

    public static PartyData party;
    public static UUID client;

    public static HashMap<UUID, PlayerData> players = new HashMap<>();

    //A map of trackers that could potentially exist on the client.
    //UUID - Id being tracked.
    //TrackerData - List of trackers tracking the given UUID and whether or not they exist on server or client.
    static HashMap<UUID, TrackerData> trackers = new HashMap<>();

}
