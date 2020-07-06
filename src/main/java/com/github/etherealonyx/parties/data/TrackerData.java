package com.github.etherealonyx.parties.data;

import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.UUID;

public class TrackerData {

    //The trackers tracking this entity.
    //UUID - The ID of the tracker.
    //Boolean - Whether or not this tracker is being tracked on the client. (True = client tracker).
    private HashMap<UUID, Boolean> trackerIds;

    //The entity itself.
    private LivingEntity entity;



}
