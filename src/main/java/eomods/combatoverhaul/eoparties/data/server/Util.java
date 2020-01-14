package eomods.combatoverhaul.eoparties.data.server;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static eomods.combatoverhaul.eoparties.data.server.ServerData.*;

public class Util {
    static final HashSet<UUID> EMPTY = new HashSet<>();

    static ArrayList<UUID> listWithSelf(UUID first, HashSet<UUID> entityTrackers) {
        ArrayList<UUID> list = new ArrayList<>();
        list.add(first);
        list.addAll(entityTrackers);
        return list;
    }

    static ArrayList<UUID> listWithSelf(UUID first, UUID entityTrackers) {
        ArrayList<UUID> list = new ArrayList<>();
        list.add(first);
        list.add(entityTrackers);
        return list;
    }

    static ArrayList<UUID> listWithoutSelf(HashSet<UUID> party, UUID player) {
        ArrayList<UUID> list = new ArrayList<>(party);
        list.remove(player);
        return list;
    }

    static ServerPlayerEntity getPlayer(UUID id) {
        return (ServerPlayerEntity) livingMembers.get(id).getPlayer();
    }

    static NetworkManager getNet(UUID id) {
        return getPlayer(id).connection.netManager;
    }

    static NetworkManager getNet(ServerPlayerEntity player) {
        return player.connection.netManager;
    }

    static boolean isOnline(UUID id) {
        return livingMembers.get(id).getPlayer() != null;
    }

    public static String getName(UUID id) {
        return livingMembers.get(id).getName();
    }

    static HashSet<UUID> getParty(UUID player) {
        for (HashSet<UUID> party : parties) {
            if (party.contains(player))
                return party;
        }
        return EMPTY;
    }

    public static boolean hasParty(UUID playerOrPet) {
        //Check if entity is found in any parties.
        for (HashSet<UUID> parties : parties) {
            if (parties.contains(playerOrPet))
                //partyMember was a player that belongs to a party.
                return true;
        }
        return false;
    }

    static HashSet<UUID> getSubParty(UUID player) {
        return subParties.getOrDefault(player, EMPTY);
    }

    public static boolean notMe(UUID uniqueID) {
        return Minecraft.getInstance().player.getUniqueID() != uniqueID;
    }

    public static boolean hasSubParty(UUID owner) {
        return subParties.containsKey(owner);
    }
}
