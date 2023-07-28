package io.sedu.mc.parties.api.events;

import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;

//This event is triggered when a party is being deleted.
public class PartyDisbandEvent extends Event {

    //The ID of the party that triggered this event.
    private final UUID id;

    //Constructor used to trigger this event. It requires the UUID of the party.
    public PartyDisbandEvent(UUID id) {
        this.id = id;
    }

    //Returns the UUID of the party.
    public UUID id() {
        return id;
    }
}
