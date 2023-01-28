package io.sedu.mc.parties.data;

import java.util.UUID;

public class TrackerData {
    private final UUID playerToTrack;
    private boolean trackedOnServer;

    public TrackerData(UUID playerToTrack) {
        this.playerToTrack = playerToTrack;
        trackedOnServer = true;
    }

    public boolean isTrackedOnServer() {
        return trackedOnServer;
    }

    public void moveToClient() {
        trackedOnServer = false;
    }

    public void moveToServer() {
        trackedOnServer = true;
    }
}
