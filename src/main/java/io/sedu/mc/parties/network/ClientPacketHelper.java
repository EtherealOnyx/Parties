package io.sedu.mc.parties.network;

import io.sedu.mc.parties.client.ClientPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.util.*;

import static io.sedu.mc.parties.client.ClientPlayerData.*;
import static io.sedu.mc.parties.data.Util.getClientPlayer;

public class ClientPacketHelper {

    private static boolean debug = true;
    static void msgDebug(String msg) {
        //if (debug)
            Minecraft.getInstance().player.sendMessage(new TextComponent(msg), Minecraft.getInstance().player.getUUID());
    }

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
                getClientPlayer(uuid).setOnline();
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
                getClientPlayer(uuid).setOffline();
                msg(getName(uuid) + " is now offline.");
            } catch (NullPointerException e) {
                error();
                e.printStackTrace();
            }
        });
    }

    public static void addMembers(ArrayList<UUID> list) {
        try {
            if (partySize() == 0) {
                ClientPlayerData.addSelf();
            }
            list.forEach(ClientPlayerData::addClientMember);
        } catch (NullPointerException e) {
            error();
            e.printStackTrace();
        }
        msg("You join the party.");
        //Try to see if we can track any of our new members on the client.
        Minecraft.getInstance().level.players().forEach(player -> {
            if (ClientPlayerData.playerList.containsKey(player.getUUID())) {
                ClientPacketHelper.sendTrackerToClient(player);
            }
        });
    }

    public static void refreshClientOnDimChange() {
        ClientPlayerData.playerList.forEach((id, data) -> {
               ClientPacketHelper.sendTrackerToServer(id);
        });
    }



    public static void changeLeader(ArrayList<UUID> list) {
        //list should always be size 1 here.
        ClientPlayerData.changeLeader(list.get(0));
        msg(getName(list.get(0)) + " is now the party leader.");
    }

    public static void dropParty() {
        ClientPlayerData.reset();
        msg("You left the party.");
    }

    public static void removePartyMemberDropped(UUID uuid) {
        msg(getName(uuid) + " left the party.");
        playerList.remove(uuid);
        playerOrderedList.remove(uuid);
    }

    public static void dropPartyKicked() {
        ClientPlayerData.reset();
        msg("You have been kicked from the party.");
    }

    public static void removePartyMemberKicked(UUID uuid) {
        msg(getName(uuid) + " was kicked from the party.");
        playerList.remove(uuid);
        playerOrderedList.remove(uuid);
    }

    public static void disbandParty() {
        ClientPlayerData.reset();
        msg("Party disbanded.");
    }

    public static void sendTrackerToClient(Player entity) {
        playerList.get(entity.getUUID()).setClientPlayer(entity);
        msgDebug("You are now tracking " + getName(entity.getUUID()) + " on the client.");
        PartiesPacketHandler.sendToServer(new ServerPacketData(0, entity.getUUID()));
    }

    public static void sendTrackerToServer(UUID id) {
        playerList.get(id).removeClientPlayer();
        msgDebug("You are no longer tracking " + getName(id) + " on the client.");
        PartiesPacketHandler.sendToServer(new ServerPacketData(1, id));
    }

    public static void setLeader() {
        ClientPlayerData.changeLeader(Minecraft.getInstance().player.getUUID());
        msg("You are now the party leader.");
    }
}
