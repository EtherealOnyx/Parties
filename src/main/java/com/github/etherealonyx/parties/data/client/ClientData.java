package com.github.etherealonyx.parties.data.client;

import com.github.etherealonyx.parties.data.PartyData;
import com.github.etherealonyx.parties.data.TrackerData;

import java.util.HashMap;
import java.util.UUID;

public class ClientData {

    //The party on the client.
    static PartyData party;

    //A map of trackers that could potentially exist on the client.
    static HashMap<UUID, TrackerData> trackers = new HashMap<>();

}
