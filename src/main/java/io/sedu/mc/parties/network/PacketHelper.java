package io.sedu.mc.parties.network;

import io.sedu.mc.parties.data.PartyData;
import io.sedu.mc.parties.data.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.UUID;

import static io.sedu.mc.parties.data.Util.getClientPlayer;
import static io.sedu.mc.parties.data.Util.getPlayer;

public class PacketHelper {

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
                msg(uuid + " is now online.");
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
                msg(uuid + " is now offline.");
            } catch (NullPointerException e) {
                error();
                e.printStackTrace();
            }
        });
    }

    public static void addMembers(ArrayList<UUID> list) {
        try {
            if (PlayerData.clientList.size() == 0)
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
        msg(uuid + " is now the party leader.");
    }

    public static void dropParty() {
        PlayerData.clientList = null;
        msg("You left the party.");
    }

    public static void removePartyMemberDropped(UUID uuid) {
        PlayerData.clientList.remove(getClientPlayer(uuid));
        msg(uuid + " left the party.");
    }

    public static void dropPartyKicked() {
        PlayerData.clientList = null;
        msg("You have been kicked from the party.");
    }

    public static void removePartyMemberKicked(UUID uuid) {
        PlayerData.clientList.remove(getClientPlayer(uuid));
        msg(uuid + " was kicked from the party.");
    }

    public static void disbandParty() {
        PlayerData.clientList = null;
        msg("Party disbanded.");
    }
}
