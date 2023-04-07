package io.sedu.mc.parties.network;

import io.sedu.mc.parties.client.overlay.ClientPlayerData;
import io.sedu.mc.parties.client.overlay.gui.HoverScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.UUID;

import static io.sedu.mc.parties.client.overlay.ClientPlayerData.*;
import static io.sedu.mc.parties.data.Util.getClientPlayer;

public class ClientPacketHelper {

    private static final boolean debug = false;
    static void msgDebug(MutableComponent msg) {
        if (debug)
            Minecraft.getInstance().player.sendMessage(msg, Minecraft.getInstance().player.getUUID());
    }

    static void msgDebug(String msg) {
        msgDebug(new TextComponent(msg).withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)));
    }


    private static void msg(MutableComponent msg) {
        Minecraft.getInstance().player.displayClientMessage(msg, true);
    }


    private static void error() {
        
        Thread.dumpStack();
    }

    public static void markOnline(ArrayList<UUID> list) {
        list.forEach(uuid -> {
            try {
                getClientPlayer(uuid).setOnline();
                msgDebug(new TextComponent(getName(uuid)).withStyle(ChatFormatting.YELLOW).append(new TextComponent(" is now online.").withStyle(ChatFormatting.AQUA)));
            } catch (NullPointerException e) {
                error();
                e.printStackTrace();
            }
        });
        Minecraft.getInstance().player.playSound(SoundEvents.NOTE_BLOCK_CHIME, .5f, 1.25f);
        Minecraft.getInstance().player.playSound(SoundEvents.NOTE_BLOCK_CHIME, .25f, 1f);
    }

    public static void markOffline(ArrayList<UUID> list) {
        list.forEach(uuid -> {
            try {
                getClientPlayer(uuid).setOffline();
                msgDebug(new TextComponent(getName(uuid)).withStyle(ChatFormatting.YELLOW).append(new TextComponent(" is now offline.").withStyle(ChatFormatting.AQUA)));
            } catch (NullPointerException e) {
                error();
                e.printStackTrace();
            }
        });
        Minecraft.getInstance().player.playSound(SoundEvents.NOTE_BLOCK_XYLOPHONE, .5f, .25f);
        Minecraft.getInstance().player.playSound(SoundEvents.NOTE_BLOCK_BIT, .25f, 1f);
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
        msg(new TranslatableComponent("messages.sedparties.phandler.joinparty").withStyle(ChatFormatting.AQUA));
        //Try to see if we can track any of our new members on the client.
        Minecraft.getInstance().level.players().forEach(player -> {
            if (ClientPlayerData.playerList.containsKey(player.getUUID())) {
                ClientPacketHelper.sendTrackerToClient(player);
            }
        });

        Minecraft.getInstance().player.playSound(SoundEvents.BEACON_ACTIVATE, .25f, 1f);
        HoverScreen.reInit();
    }

    public static void refreshClientOnDimChange() {
        ClientPlayerData.playerList.forEach((id, data) -> ClientPacketHelper.sendTrackerToServer(id));
    }



    public static void changeLeader(ArrayList<UUID> list) {
        //list should always be size 1 here.
        ClientPlayerData.changeLeader(list.get(0));
        msg((new TranslatableComponent("messages.sedparties.phandler.changeleader", getName(list.get(0))).withStyle(ChatFormatting.AQUA)));
        Minecraft.getInstance().player.playSound(SoundEvents.NOTE_BLOCK_XYLOPHONE, .5f, 1f);
        Minecraft.getInstance().player.playSound(SoundEvents.NOTE_BLOCK_CHIME, .5f, 1.25f);
    }

    public static void dropParty() {
        ClientPlayerData.reset();
        msg(new TranslatableComponent("messages.sedparties.phandler.dropparty").withStyle(ChatFormatting.DARK_AQUA));
        Minecraft.getInstance().player.playSound(SoundEvents.BEACON_DEACTIVATE, .25f, 1f);
        HoverScreen.reInit();
    }

    public static void removePartyMemberDropped(UUID uuid) {
        msg(new TranslatableComponent("messages.sedparties.phandler.leftparty", getName(uuid)).withStyle(ChatFormatting.AQUA));
        remove(uuid);
    }

    private static void remove(UUID id) {
        playerList.remove(id);
        playerOrderedList.remove(id);
        HoverScreen.reInit();
    }

    public static void dropPartyKicked() {
        ClientPlayerData.reset();
        msg(new TranslatableComponent("messages.sedparties.phandler.kickedfromparty").withStyle(ChatFormatting.AQUA));
        Minecraft.getInstance().player.playSound(SoundEvents.BEACON_DEACTIVATE, .25f, 1f);
        HoverScreen.reInit();
    }

    public static void removePartyMemberKicked(UUID uuid) {
        msg(new TranslatableComponent("messages.sedparties.phandler.kickedfrompartyother", getName(uuid)).withStyle(ChatFormatting.AQUA));
        remove(uuid);
    }

    public static void disbandParty() {
        ClientPlayerData.reset();
        msg(new TranslatableComponent("messages.sedparties.phandler.partydisband").withStyle(ChatFormatting.AQUA));
        Minecraft.getInstance().player.playSound(SoundEvents.BEACON_DEACTIVATE, .25f, 1f);
        HoverScreen.reInit();
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
        msg(new TranslatableComponent("messages.sedparties.phandler.changeleader2").withStyle(ChatFormatting.AQUA));
    }
}
