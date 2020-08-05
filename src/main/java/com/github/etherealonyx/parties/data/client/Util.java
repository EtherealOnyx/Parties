package com.github.etherealonyx.parties.data.client;

import java.util.UUID;

public class Util {

    //This checks to make sure that the UUID exists on the client.
    public static boolean exists(UUID toCheck) {
        return ClientData.players.containsKey(toCheck);
    }
}
