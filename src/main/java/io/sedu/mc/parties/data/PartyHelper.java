package io.sedu.mc.parties.data;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static io.sedu.mc.parties.data.Util.*;

public class PartyHelper {

    public static boolean invitePlayerForced(UUID initiator, UUID futureMember) {
        if (!verifyRequest(initiator, futureMember))
            return false;

        //This checks if initiator is in a party. Creates one if not.
        if (!getPlayer(initiator).hasParty()) {
            new PartyData(initiator);
        }
        return addPlayerToParty(futureMember, getPartyFromMember(initiator));
    }

    public static boolean invitePlayer(UUID initiator, UUID futureMember) {
        if (getPlayer(futureMember).isInviter(initiator)) {
            getPlayer(futureMember).removeInviter(initiator);
            return invitePlayerForced(initiator, futureMember);
        }

        return false;
    }

    //This adds the player to the given party.
    public static boolean addPlayerToParty(UUID futureMember, PartyData currentParty) {
        
        currentParty.addMember(futureMember);
        return true;
    }

    //Kicks player from party.
    public static boolean kickPlayer(UUID initiator, UUID removedMember) {
        if (!inSameParty(initiator, removedMember)) {
            
            return false;
        }
        return removePlayerFromParty(removedMember, true);
    }

    public static boolean removePlayerFromParty(UUID removedMember, boolean wasKicked) {
        //Remove player from the party.
        getPartyFromMember(removedMember).removeMember(removedMember, wasKicked);
        return true;
    }

    public static boolean leaveParty(UUID uuid) {
        if (getPlayer(uuid).hasParty()) {
            getPartyFromMember(uuid).removeMember(uuid, false);
            return true;
        }
        return false;
    }

    public static boolean giveLeader(UUID player) {
        getPartyFromMember(player).updateLeader(player);
        return true;
    }

    public static void questionPlayer(UUID initiator, UUID futureMember) {
        //This checks if futureMember is a valid player that exists on the server.
        
        if (verifyRequest(initiator, futureMember) && !getPlayer(futureMember).isInviter(initiator)) {
            
            getPlayer(futureMember).addInviter(initiator);
            //Sends message to futureMember.
            getPlayer(futureMember).getPlayer().sendMessage(
                    new TextComponent(getName(initiator)).withStyle(ChatFormatting.YELLOW
                    ).append(new TextComponent(" invites you to a party: ").withStyle(ChatFormatting.DARK_AQUA)
                    ).append(new TextComponent("Accept").withStyle(
                            style -> style.withColor(ChatFormatting.GREEN)
                                          .withUnderlined(true)
                                          .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                         "/party accept " + getName(initiator)))
                                          .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                         new TextComponent("Accept Party Invite"))))
                    ).append(" "
                    ).append(
                            new TextComponent("Decline").withStyle(
                                    style -> style.withColor(ChatFormatting.RED)
                                                  .withUnderlined(true)
                                                  .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                                 "/party decline " + getName(initiator)))
                                                  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                                 new TextComponent("Decline Party Invite")))
                            )
                    ), futureMember
            );
            getPlayer(initiator).getPlayer().sendMessage(new TextComponent("You have sent a party invite to ").withStyle(ChatFormatting.DARK_AQUA)
                                                                                     .append(new TextComponent(getName(futureMember)).withStyle(ChatFormatting.YELLOW
                                                                                     )).append(new TextComponent(".").withStyle(ChatFormatting.DARK_AQUA)), initiator);

        }


    }

    private static boolean verifyRequest(UUID initiator, UUID futureMember) {
        if (getPlayer(futureMember) == null || getPlayer(initiator) == null || initiator.equals(futureMember)) {
            
            return false;
        }
        
        //This checks if the target is currently in a party.
        if (getPlayer(futureMember).hasParty()) {
            
            return false;
        }
        

        if (getPlayer(initiator).hasParty() && !isLeader(initiator)) {
            
            return false;
        }

        
        return true;
    }

    public static boolean declineInvite(UUID initiator, UUID futureMember) {
        if (!getPlayer(futureMember).isInviter(initiator))
            return false;
        ServerPlayer p;
        if ((p = getPlayer(initiator).getPlayer()) != null) {
            p.sendMessage(new TextComponent(getName(futureMember)).withStyle(ChatFormatting.YELLOW).append(new TextComponent(
                    " has declined your party invite.").withStyle(ChatFormatting.DARK_AQUA)), initiator);
        }
        if ((p = getPlayer(futureMember).getPlayer()) != null) {
            p.sendMessage(new TextComponent("You have declined a party invite from ").withStyle(ChatFormatting.DARK_AQUA)
                 .append(new TextComponent(getName(initiator)).withStyle(ChatFormatting.YELLOW
                         )).append(new TextComponent(".").withStyle(ChatFormatting.DARK_AQUA)), futureMember);
        }
        getPlayer(futureMember).removeInviter(initiator);
        return true;
    }

    public static void dismissInvite(UUID initiator) {
        ServerPlayer p;
        if ((p = getServerPlayer(initiator)) != null) {
            p.sendMessage(new TextComponent(
                    "Your party invite has been automatically declined.").withStyle(ChatFormatting.DARK_AQUA), p.getUUID());
        }

    }

    public static void dismissInvite(PlayerData playerData, UUID initiator) {
        ServerPlayer p;
        if ((p = getServerPlayer(initiator)) != null) {
            p.sendMessage(new TextComponent(playerData.getName()).withStyle(ChatFormatting.YELLOW).append(new TextComponent(
                    " took too long to respond. Invite cancelled.").withStyle(ChatFormatting.DARK_AQUA)), initiator);
        }

        if ((p = playerData.getPlayer()) != null) {
            p.sendMessage(new TextComponent("You took too long to respond to a party request from ").withStyle(ChatFormatting.DARK_AQUA).append(new TextComponent(getName(initiator)).withStyle(ChatFormatting.YELLOW)), initiator);
        }
    }
}
