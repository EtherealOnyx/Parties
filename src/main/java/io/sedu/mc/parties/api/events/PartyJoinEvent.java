package io.sedu.mc.parties.api.events;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.data.ServerPlayerData;
import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;

import static io.sedu.mc.parties.api.helper.PlayerAPI.getServerPlayer;


/**
 * This event is triggered when the player joins the party for the first time,
 * or when they are online again. This is also triggered by the party leader
 * when the player invites another party member and the member accepts (triggered once for each member).
 */
public class PartyJoinEvent extends PlayerEvent {

    //A collection of players (by their UUID) that are tracking this player.
    //The boolean determines what side the tracking is on (true = server side). Boolean isn't used here.
    private final HashMap<UUID, Boolean> trackers;

    //The ID of the player that triggered this event.
    private final UUID id;
    private final UUID partyId;

    //Constructor used to trigger this event. It requires the player.
    public PartyJoinEvent(Player player, UUID partyId) {
        super(player);
        Parties.LOGGER.debug("New join event for {}", player.getScoreboardName());
        trackers = ServerPlayerData.playerTrackers.get(id = player.getUUID());
        this.partyId = partyId;
    }

    /*
     * This method traverses through all the players on the trackers and accepts an action with this player
     * as the origin or vice versa. This is useful for when you need to send data of the player to the tracker, AND
     * when you need to send data to the player of the tracker.
     * This version of the function also has the Player object to be utilized in the consumer action.
     *
     * NOTE: This action only sends data of the tracker to the player that triggered this event only if the tracker
     * is online (and has a valid cached player object).
     */
    public void forTrackersAndSelf(TriConsumer<UUID, UUID, Player> action) {
        if (trackers != null) {
            trackers.forEach((id, serverTracked) -> {
                getServerPlayer(id, (serverPlayer) -> action.accept(this.id, id, serverPlayer));
                action.accept(id, this.id, getPlayer());
            });
        }
    }

    /*
     * This method traverses through all the players on the trackers and accepts an action with this player
     * as the origin or vice versa. This is useful for when you need to send data of the player to the tracker, AND
     * when you need to send data to the player of the tracker.
     * This version of the function only utilizes two UUIDs.
     */
    public void forTrackersAndSelf(BiConsumer<UUID, UUID> action) {
        if (trackers != null) {
            trackers.forEach((id, serverTracked) -> {
                action.accept(this.id, id);
                action.accept(id, this.id);
            });
        }
    }

    //This method simply returns the party ID associated with the party creation.
    public UUID getPartyId() {
        return partyId;
    }
}
