package io.sedu.mc.parties.network;

import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import static io.sedu.mc.parties.data.Util.getPlayer;
import static io.sedu.mc.parties.data.PlayerData.*;

public class ClientPacketHelper {

    private static void msg(String msg) {
        Minecraft.getInstance().player.sendMessage(new TextComponent(msg), Minecraft.getInstance().player.getUUID());
    }

    private static void error() {
        System.out.println("Error executing packet task.");
        Thread.dumpStack();
    }

    public static void markOnline(ArrayList<UUID> list) {
        list.forEach(uuid -> {
            try {
                getPlayer(uuid).setOnline();
                msg(getName(uuid) + " is now online.");
            } catch (NullPointerException e) {
                error();
                e.printStackTrace();
            }
        });
    }

    public static void markOffline(ArrayList<UUID> list) {
        list.forEach(uuid -> {
            try {
                getPlayer(uuid).setOffline();
                msg(getName(uuid) + " is now offline.");
            } catch (NullPointerException e) {
                error();
                e.printStackTrace();
            }
        });
    }

    public static void addMembers(ArrayList<UUID> list) {
        try {
            if (PlayerData.partySize() == 0)
                PartyData.addClientMember(Minecraft.getInstance().player.getUUID());
            list.forEach(PartyData::addClientMember);
        } catch (NullPointerException e) {
            error();
            e.printStackTrace();
        }
        msg("You join the party.");
        //Try to see if we can track any of our new members on the client.
        Iterator it = potentialTracks.values().iterator();
        Player p;
        while (it.hasNext()) {
            p = (Player) it.next();
            if (playerList.containsKey(p.getUUID())) {
                playerList.get(p.getUUID()).setClientPlayer(PlayerData.potentialTracks.remove(p.getUUID()));
                ClientPacketHelper.sendTrackerToServer(p);
            }
        }
    }



    public static void changeLeader(ArrayList<UUID> list) {
        //list should always be size 1 here.
        UUID uuid = list.get(0);
        leader = list.get(0);
        msg(getName(uuid) + " is now the party leader.");
    }

    public static void dropParty() {
        playerList.clear();
        msg("You left the party.");
    }

    public static void removePartyMemberDropped(UUID uuid) {
        int index = playerList.get(uuid).getIndex();
        //Move tracker to potential trackers if tracked on client.
        if (!playerList.get(uuid).isTrackedOnServer()) {
            potentialTracks.put(uuid, playerList.get(uuid).getClientPlayer());
        }
        playerList.remove(uuid);
        updatePartyIndex(index);
        msg(getName(uuid) + " left the party.");
    }

    public static void dropPartyKicked() {
        playerList.clear();
        msg("You have been kicked from the party.");
    }

    public static void removePartyMemberKicked(UUID uuid) {
        int index = playerList.get(uuid).getIndex();
        //Move tracker to potential trackers if tracked on client.
        if (!playerList.get(uuid).isTrackedOnServer()) {
            potentialTracks.put(uuid, playerList.get(uuid).getClientPlayer());
        }
        playerList.remove(uuid);
        updatePartyIndex(index);
        msg(getName(uuid) + " was kicked from the party.");
    }

    public static void disbandParty() {
        playerList.clear();
        msg("Party disbanded.");
    }

    public static void sendTrackerToClient(Player entity) {
        playerList.get(entity.getUUID()).setClientPlayer(entity);
        msg("You are now tracking " + getName(entity.getUUID()) + " on the client.");
        PartiesPacketHandler.sendToServer(new ServerPacketData(0, entity.getUUID()));
    }

    public static void sendTrackerToServer(Player entity) {
        playerList.get(entity.getUUID()).removeClientPlayer();
        msg("You are no longer tracking " + getName(entity.getUUID()) + " on the client.");
    }

    public static void setLeader() {
        leader = Minecraft.getInstance().player.getUUID();
        msg("You are now the party leader.");
    }
}
