package io.sedu.mc.parties.network;

import io.sedu.mc.parties.data.PlayerData;
import io.sedu.mc.parties.data.Util;
import io.sedu.mc.parties.events.PartyJoinEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.sedu.mc.parties.data.ServerConfigData.playerMessageCooldown;
import static io.sedu.mc.parties.data.Util.*;

public class ServerPacketHelper {

    public static void sendNewMember(UUID futureMember, ArrayList<UUID> party) {
        //Send each member to future party member.
        
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, party), getNormalServerPlayer(futureMember));
        //Send each member's properties to future party member.
        party.forEach(id -> {
            InfoPacketHelper.sendName(futureMember, id);
        });

        //Send future member to each party member.
        party.forEach(id -> {
            
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, futureMember), getNormalServerPlayer(id));
            //Send future member properties to each party member.
            InfoPacketHelper.sendName(id, futureMember);
            //Tell party member that new member is Online
            if (isOnline(futureMember)) {
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, futureMember), getNormalServerPlayer(id));
                InfoPacketHelper.forceUpdate(id, futureMember, true);
            }

            //Tell newly online player that this other member is also online.
            if (isOnline(id)) {
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, id), getNormalServerPlayer(futureMember));
                InfoPacketHelper.forceUpdate(futureMember, id, true);
            }

        });

        //Send leader to future party member.
        
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(3, getPartyFromMember(party.get(0)).getLeader()),
                                          getNormalServerPlayer(futureMember));
    }

    public static void sendRemoveMember(UUID removedMember, ArrayList<UUID> party, boolean wasKicked) {
        int i = (wasKicked) ? 5 : 4;
        party.forEach(id -> {
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(i, removedMember), getNormalServerPlayer(id));
        });
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(i), getNormalServerPlayer(removedMember));
    }

    public static void disband(ArrayList<UUID> party) {
        party.forEach(id -> {
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(6), getNormalServerPlayer(id));
        });
    }

    public static void sendNewLeader(UUID newLeader, ArrayList<UUID> party) {
        party.forEach(id -> {
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(3, newLeader), getNormalServerPlayer(id));
        });
    }

    public static void sendOnline(ServerPlayer player) {
        if (Util.hasParty(player.getUUID())) {
            ArrayList<UUID> mParty = new ArrayList<>(Util.getPartyFromMember(player.getUUID()).getMembers());
            mParty.remove(player.getUUID());
            //Pretend the party just formed and add all players for new online member.
            PartiesPacketHandler.sendToPlayer(new ClientPacketData(2, mParty), player);
            mParty.forEach(id -> {
                //Tell online player the current party member's name.
                InfoPacketHelper.sendName(player, id);
                //Tell party members that this player is now online.
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, player.getUUID()), getNormalServerPlayer(id));
                InfoPacketHelper.forceUpdate(id, player.getUUID(), true);
                //Tell newly online player that this other member is also online.
                if (isOnline(id)) {
                    PartiesPacketHandler.sendToPlayer(new ClientPacketData(0, id), player);
                    InfoPacketHelper.forceUpdate(player.getUUID(), id, true);
                }

            });
            //Tell the online party member who the leader is.
            PartiesPacketHandler.sendToPlayer(
                    new ClientPacketData(3, getPartyFromMember(player.getUUID()).getLeader()), player);
            //API Helper
            MinecraftForge.EVENT_BUS.post(new PartyJoinEvent(player));
        }
        if(!player.isDeadOrDying())
            player.getActiveEffects().forEach(effect -> InfoPacketHelper.sendEffect(player.getUUID(), MobEffect.getId(effect.getEffect()), effect.getDuration(), effect.getAmplifier()));
    }

    public static void sendOffline(UUID player) {
        if (Util.hasParty(player)) {
            Util.getPartyFromMember(player).getMembers().forEach(id -> {
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(1, player), getNormalServerPlayer(id));
            });
        }
    }

    public static void trackerToClient(UUID tracker, UUID playerToTrack) {
        
        PlayerData.changeTracker(tracker, playerToTrack, false);
    }

    public static void trackerToServer(UUID tracker, UUID playerToTrack) {
        PlayerData.changeTracker(tracker, playerToTrack, true);
    }

    public static void sendNewLeader(UUID initiator) {
        PartiesPacketHandler.sendToPlayer(new ClientPacketData(3), getNormalServerPlayer(initiator));
    }

    public static void sendMessageToAll(List<ServerPlayer> playerList, ServerPlayer sender, String data) {
        if (PlayerData.isOnMessageCd(sender.getUUID())) return;
        playerList.forEach((p) -> {
            p.sendMessage(new TextComponent("<").append(sender.getName()).append(new TextComponent("> ")).append(new TextComponent("[").withStyle(ChatFormatting.DARK_AQUA)).append(new TextComponent("Preset").withStyle(style -> style.withColor(ChatFormatting.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, data)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("gui.sedparties.tooltip.hoverlink"))))).append(new TextComponent("]").withStyle(ChatFormatting.DARK_AQUA)).append(new TextComponent(" (Click to Copy)").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC)), ChatType.CHAT, sender.getUUID());
            p.sendMessage(new TranslatableComponent("gui.sedparties.tooltip.linkpaste").withStyle(style -> style.withColor(ChatFormatting.GRAY)
                                                                                                            .withItalic(true)
                                                                                                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/applypreset"))
                                                                                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("gui.sedparties.tooltip.linkpastedesc")))), ChatType.SYSTEM, p.getUUID());

        });
        PlayerData.messageCd.add(new PlayerData.MessageCdHolder(sender.getUUID(), playerMessageCooldown));
    }
}
