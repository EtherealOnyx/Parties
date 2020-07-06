package com.github.etherealonyx.parties.data;

import com.github.etherealonyx.parties.data.server.PlayerData;
import com.github.etherealonyx.parties.data.server.ServerData;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.UUID;

import static com.github.etherealonyx.parties.data.Util.getMember;
import static com.github.etherealonyx.parties.data.Util.getParty;

public class PlayerHelper {

    public static void onPlayerJoin(ServerPlayerEntity player) {
        if (!ServerData.players.containsKey(player.getUniqueID())) {
            ServerData.players.put(player.getUniqueID(), new PlayerData());
        } else {
            ServerData.players.get(player.getUniqueID()).markOnline();
        }

        //Checks if the player is a party member and whether or not the party leader needs to be changed.
        //This ensures there's always going to be a party leader online, if at least one member is online in the party.
        if (ServerData.players.get(player.getUniqueID()).hasParty() &&
                !getMember(getParty(player.getUniqueID()).getLeader()).isOnline()) {
            PartyHelper.updateLeader(getParty(player.getUniqueID()), player.getUniqueID());
        }
    }

    public static void onPlayerLeave(UUID id) {
        //Marks the player as offline.
        ServerData.players.get(id).markOffline();

        //Checks whether or not the offline player was the leader, if so, attempt to change the party leader. If
        // everyone else is offline then they stayed leader.
        if (getMember(id).isLeader()) {
            if (!PartyHelper.updateLeader(getParty(id))) {
                System.out.println("Everyone else was offline! Party leader remains...");
            }
        }
    }
}
