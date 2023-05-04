package io.sedu.mc.parties.data;

import io.sedu.mc.parties.api.openpac.PACCompatManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.sedu.mc.parties.data.Util.*;

public class PartyHelper {

    public static boolean invitePlayerForced(UUID initiator, UUID futureMember) {
        if (!verifyRequest(initiator, futureMember))
            return false;

        //Open-PAC Support
        if (ServerConfigData.isPartySyncEnabled()) {
            return PACCompatManager.getHandler().addPartyMember(initiator, futureMember, false);
        }

        //This checks if initiator is in a party. Creates one if not.
        if (!Objects.requireNonNull(getNormalPlayer(initiator)).hasParty()) {
            new PartyData(initiator);
        }
        return addPlayerToParty(futureMember, Objects.requireNonNull(getPartyFromMember(initiator)));
    }

    public static boolean acceptInvite(UUID initiator, UUID futureMember) {
        PlayerData fM;
        if ((fM = getNormalPlayer(futureMember)) == null) return false;
        if (fM.isInviter(initiator)) {
            fM.removeInviter(initiator);
            return invitePlayerForced(initiator, futureMember);
        }

        return false;
    }

    public static boolean acceptInvite(UUID futureMember) {
        AtomicBoolean ret = new AtomicBoolean(false);
        getPlayer(futureMember, playerData -> playerData.ifInviterExists((inviter) -> ret.set(acceptInvite(inviter, futureMember))));
        return ret.get();
    }

    public static boolean declineInvite(UUID initiator, UUID futureMember) {
        PlayerData pD;
        if ((pD = getNormalPlayer(futureMember)) == null) return false;
        if (!pD.isInviter(initiator)) return false;
        ServerPlayer p;
        if ((p = getNormalServerPlayer(initiator)) != null) {
            p.sendMessage(new TranslatableComponent("messages.sedparties.phandler.invitedeclined", getName(futureMember)).withStyle(ChatFormatting.DARK_AQUA), initiator);
        }
        if ((p = getNormalServerPlayer(futureMember)) != null) {
            p.sendMessage(new TranslatableComponent("messages.sedparties.phandler.declineinvite", getName(initiator)).withStyle(ChatFormatting.DARK_AQUA), futureMember);
        }
        getPlayer(futureMember, playerData -> playerData.removeInviter(initiator));
        return true;
    }

    public static boolean declineInvite(UUID futureMember) {
        AtomicBoolean ret = new AtomicBoolean(false);
        getPlayer(futureMember, playerData -> playerData.ifInviterExists((inviter) -> ret.set(declineInvite(inviter, futureMember))));
        return ret.get();
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
        //Open-PAC Support
        if (ServerConfigData.isPartySyncEnabled()) {
            return PACCompatManager.getHandler().removePartyMember(initiator, removedMember, false);
        }

        return removePlayerFromParty(removedMember, true);
    }

    public static boolean removePlayerFromParty(UUID removedMember, boolean wasKicked) {
        //Remove player from the party.
        Objects.requireNonNull(getPartyFromMember(removedMember)).removeMember(removedMember, wasKicked);
        return true;
    }

    public static boolean leaveParty(UUID uuid) {
        PlayerData p;
        if ((p = getNormalPlayer(uuid)) != null && p.hasParty()) {
            Objects.requireNonNull(getPartyFromMember(uuid)).removeMember(uuid, false);
            return true;
        }
        return false;
    }

    public static boolean giveLeader(UUID player) {
        Objects.requireNonNull(getPartyFromMember(player)).updateLeader(player);
        return true;
    }

    public static void questionPlayer(UUID initiator, UUID futureMember) {
        //This checks if futureMember is a valid player that exists on the server.
        PlayerData fM;
        if (verifyRequest(initiator, futureMember) && !((Objects.requireNonNull(fM = getNormalPlayer(futureMember))).isInviter(initiator))) {
            
            fM.addInviter(initiator);
            //Sends message to futureMember.
            fM.getPlayer().sendMessage(
                    new TranslatableComponent("messages.sedparties.phandler.receivedinvite", getName(initiator)).withStyle(ChatFormatting.DARK_AQUA)
                    , futureMember);

            fM.getPlayer().sendMessage(
                    new TranslatableComponent("messages.sedparties.phandler.receivedinvite2").withStyle(ChatFormatting.DARK_AQUA)
                                                                                 .append(new TranslatableComponent("messages.sedparties.phandler.receivedinvite3").withStyle(
                            style -> style.withColor(ChatFormatting.GREEN)
                                          .withUnderlined(true)
                                          .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                         "/party accept " + getName(initiator)))
                                          .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                         new TranslatableComponent("messages.sedparties.phandler.accepttooltip"))))
                    ).append(" "
                    ).append(
                            new TranslatableComponent("messages.sedparties.phandler.receivedinvite4").withStyle(
                                    style -> style.withColor(ChatFormatting.RED)
                                                  .withUnderlined(true)
                                                  .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                                 "/party decline " + getName(initiator)))
                                                  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                                 new TranslatableComponent("messages.sedparties.phandler.declinetooltip")))
                            )
                    )
                    , futureMember);

            //TODO: Support changing /party to /p, etc
            getServerPlayer(futureMember, serverPlayer -> serverPlayer.sendMessage(
                    new TranslatableComponent("messages.sedparties.phandler.receivedinvite5").withStyle(ChatFormatting.DARK_AQUA)
                                                                                             .append(new TextComponent("/party accept").withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)))
                                                                                             .append(new TextComponent(" | ").withStyle(ChatFormatting.DARK_AQUA))
                                                                                             .append(new TextComponent("/party decline").withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)))
                    , futureMember));

            getServerPlayer(initiator, serverPlayer -> serverPlayer.sendMessage(new TranslatableComponent("messages.sedparties.phandler.sendinvite",
                                                                                                      getName(futureMember)).withStyle(ChatFormatting.DARK_AQUA), initiator));
        }


    }

    private static boolean verifyRequest(UUID initiator, UUID futureMember) {
        ServerPlayer p;
        if (getNormalPlayer(futureMember) == null || (p = getNormalServerPlayer(initiator)) == null || initiator.equals(futureMember)) {
            
            return false;
        }
        
        //This checks if the target is currently in a party.
        if (Objects.requireNonNull(getNormalPlayer(futureMember)).hasParty()) {
            p.sendMessage(new TranslatableComponent(
                    "messages.sedparties.phandler.alreadyhasparty", getName(futureMember)).withStyle(ChatFormatting.DARK_AQUA), initiator);
            return false;
        }
        if (Objects.requireNonNull(getNormalPlayer(initiator)).hasParty()) {
            if (!isLeader(initiator)) {
                p.sendMessage(new TranslatableComponent(
                        "messages.sedparties.phandler.noinviteperms").withStyle(ChatFormatting.DARK_AQUA), initiator);
                return false;
            }
            if (Objects.requireNonNull(getPartyFromMember(initiator)).isFull()) {
                p.sendMessage(new TranslatableComponent(
                        "messages.sedparties.phandler.partyfull").withStyle(ChatFormatting.DARK_AQUA), initiator);
                return false;
            }
        }
        return true;
    }

    public static void dismissInvite(UUID initiator) {
        getServerPlayer(initiator, serverPlayer -> {
            serverPlayer.sendMessage(new TranslatableComponent(
                    "messages.sedparties.phandler.declineinviteauto").withStyle(ChatFormatting.DARK_AQUA), initiator);
        });
    }

    public static void dismissInvite(PlayerData playerData, UUID initiator) {
        ServerPlayer p;
        getServerPlayer(initiator, serverPlayer -> {
            serverPlayer.sendMessage(new TranslatableComponent(
                    "messages.sedparties.phandler.declineinviteauto2", playerData.getName()).withStyle(ChatFormatting.DARK_AQUA), initiator);
        });

        if ((p = playerData.getPlayer()) != null) {
            p.sendMessage(new TranslatableComponent("messages.sedparties.phandler.declineinviteauto3", getName(initiator)).withStyle(ChatFormatting.DARK_AQUA), initiator);
        }
    }
}
