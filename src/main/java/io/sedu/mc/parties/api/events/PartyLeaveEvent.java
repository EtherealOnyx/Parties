package io.sedu.mc.parties.api.events;

import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;

//This event is triggered when a player is being removed from a party. This is called if the player leaves the party,
//got kicked from said party, or the party is being disbanded.
public class PartyLeaveEvent extends Event {
    //The ID of the player that was removed from the party.
    private final UUID playerId;
    private final UUID partyId;

    //Constructor used to trigger this event. It requires the ID of the player that left and the ID of the party involved.
    public PartyLeaveEvent(UUID playerId, UUID partyId) {
        this.playerId = playerId;
        this.partyId = partyId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getPartyId() {
        return partyId;
    }
}
