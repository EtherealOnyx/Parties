package io.sedu.mc.parties.events;

import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.util.TriConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;

import static io.sedu.mc.parties.data.Util.getServerPlayer;

//This event is triggered when the player rejoins the party (by being online again)
// or when they join the party for the first time.
public class PartyJoinEvent extends PlayerEvent {
    private final HashMap<UUID, Boolean> trackers;
    private final UUID id;
    public PartyJoinEvent(Player player) {
        super(player);
        trackers = PlayerData.playerTrackers.get(id = player.getUUID());
    }

    public void forTrackersAndSelf(TriConsumer<UUID, UUID, Player> action) {
        if (trackers != null) {
            trackers.forEach((id, serverTracked) -> {
                getServerPlayer(id, (serverPlayer) -> action.accept(this.id, id, serverPlayer));
                action.accept(id, this.id, getPlayer());
            });
        }
    }

    public void forTrackersAndSelf(BiConsumer<UUID, UUID> action) {
        if (trackers != null) {
            trackers.forEach((id, serverTracked) -> {
                action.accept(this.id, id);
                action.accept(id, this.id);
            });
        }
    }
}
