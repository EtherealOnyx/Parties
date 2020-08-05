package com.github.etherealonyx.parties.data.server;

import com.github.etherealonyx.parties.data.PlayerData;
import com.github.etherealonyx.parties.data.TrackerData;

import java.util.HashMap;
import java.util.UUID;

public class ServerData {

    //A map of trackers that exist.
    public static HashMap<UUID, TrackerData> trackers = new HashMap<>();

    //A map of players that exist.
    public static HashMap<UUID, PlayerData> players = new HashMap<>();


}
