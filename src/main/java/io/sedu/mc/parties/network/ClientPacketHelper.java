package io.sedu.mc.parties.network;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.util.*;

import static io.sedu.mc.parties.client.overlay.ClientPlayerData.*;
import static io.sedu.mc.parties.data.Util.getClientPlayer;

public class ClientPacketHelper {

    private static boolean debug = false;
    static void msgDebug(MutableComponent msg) {
        //if (debug)
            //Minecraft.getInstance().player.sendMessage(msg, Minecraft.getInstance().player.getUUID());
    }

    static void msgDebug(String msg) {
        msgDebug(new TextComponent(msg).withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)));
    }


    private static void msg(MutableComponent msg) {
        Minecraft.getInstance().player.sendMessage(msg, Minecraft.getInstance().player.getUUID());
    }


    private static void error() {
        System.out.println("Error executing packet task.");
        Thread.dumpStack();
    }

    public static void markOnline(ArrayList<UUID> list) {
        list.forEach(uuid -> {
            try {
                getClientPlayer(uuid).setOnline();
                msgDebug(new TextComponent(getName(uuid)).withStyle(ChatFormatting.YELLOW).append(new TextComponent(" is now online.")));
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
                msgDebug(new TextComponent(getName(uuid)).withStyle(ChatFormatting.YELLOW).append(new TextComponent(" is now offline.")));
            } catch (NullPointerException e) {
                error();
                e.printStackTrace();
            }
        });
    }

    public static void addMembers(ArrayList<UUID> list) {
        try {
            if (partySize() == 0) {
                ClientPlayerData.addSelfParty();
            }
            list.forEach(ClientPlayerData::addClientMember);
        } catch (NullPointerException e) {
            error();
            e.printStackTrace();
        }
        msg(new TextComponent("You join the party.").withStyle(ChatFormatting.DARK_AQUA));
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
        msg(new TextComponent(getName(list.get(0))).withStyle(ChatFormatting.YELLOW).append(new TextComponent(" is now the party leader.").withStyle(ChatFormatting.DARK_AQUA)));
    }

    public static void dropParty() {
        ClientPlayerData.reset();
        msg(new TextComponent("You left the party.").withStyle(ChatFormatting.DARK_AQUA));
    }

    public static void removePartyMemberDropped(UUID uuid) {
        msg(new TextComponent(getName(uuid)).withStyle(ChatFormatting.YELLOW).append(new TextComponent(" left the party.").withStyle(ChatFormatting.DARK_AQUA)));
        playerList.remove(uuid);
        playerOrderedList.remove(uuid);
    }

    public static void dropPartyKicked() {
        ClientPlayerData.reset();
        msg(new TextComponent("You have been kicked from the party.").withStyle(ChatFormatting.DARK_AQUA));
    }

    public static void removePartyMemberKicked(UUID uuid) {
        msg(new TextComponent(getName(uuid)).withStyle(ChatFormatting.YELLOW).append(new TextComponent(" was kicked from the party.").withStyle(ChatFormatting.DARK_AQUA)));
        playerList.remove(uuid);
        playerOrderedList.remove(uuid);
    }

    public static void disbandParty() {
        ClientPlayerData.reset();
        msg(new TextComponent("Party disbanded.").withStyle(ChatFormatting.DARK_AQUA));
    }

    public static void sendTrackerToClient(Player entity) {
        playerList.get(entity.getUUID()).setClientPlayer(entity);
        if (entity.getUUID().equals(Minecraft.getInstance().player.getUUID()))
            return;
        msgDebug("You are now tracking " + getName(entity.getUUID()) + " on the client.");
        PartiesPacketHandler.sendToServer(new ServerPacketData(0, entity.getUUID()));
    }

    public static void sendTrackerToServer(UUID id) {
        playerList.get(id).removeClientPlayer();
        if (id.equals(Minecraft.getInstance().player.getUUID()))
            return;
        msgDebug("You are no longer tracking " + getName(id) + " on the client.");
        PartiesPacketHandler.sendToServer(new ServerPacketData(1, id));
    }

    public static void setLeader() {
        ClientPlayerData.changeLeader(Minecraft.getInstance().player.getUUID());
        msg(new TextComponent("You are now the party leader.").withStyle(ChatFormatting.DARK_AQUA));
    }
}
