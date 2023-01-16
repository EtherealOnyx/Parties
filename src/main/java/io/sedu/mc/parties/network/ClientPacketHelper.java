package io.sedu.mc.parties.network;

import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.UUID;

import static io.sedu.mc.parties.data.Util.getPlayer;

public class ClientPacketHelper {

    private static void msg(String msg) {
        Minecraft.getInstance().player.sendMessage(new TextComponent(msg), Minecraft.getInstance().player.getUUID());
    }

    private static String getName(UUID id) {
        for (PlayerInfo pi : Minecraft.getInstance().player.connection.getOnlinePlayers()) {
            if (pi.getProfile().getId().equals(id))
                return pi.getProfile().getName();
        }
        return "";
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
    }



    public static void changeLeader(ArrayList<UUID> list) {
        //list should always be size 1 here.
        UUID uuid = list.get(0);
        PlayerData.leader = list.get(0);
        msg(getName(uuid) + " is now the party leader.");
    }

    public static void dropParty() {
        PlayerData.playerList.clear();
        msg("You left the party.");
    }

    public static void removePartyMemberDropped(UUID uuid) {
        int index = PlayerData.playerList.get(uuid).getIndex();
        PlayerData.playerList.remove(uuid);
        PlayerData.updatePartyIndex(index);
        msg(getName(uuid) + " left the party.");
    }

    public static void dropPartyKicked() {
        PlayerData.playerList.clear();
        msg("You have been kicked from the party.");
    }

    public static void removePartyMemberKicked(UUID uuid) {
        int index = PlayerData.playerList.get(uuid).getIndex();
        PlayerData.playerList.remove(uuid);
        PlayerData.updatePartyIndex(index);
        msg(getName(uuid) + " was kicked from the party.");
    }

    public static void disbandParty() {
        PlayerData.playerList.clear();
        msg("Party disbanded.");
    }
}
