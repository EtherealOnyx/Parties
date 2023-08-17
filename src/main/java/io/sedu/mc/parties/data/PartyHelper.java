package io.sedu.mc.parties.data;

import io.sedu.mc.parties.api.events.PartyJoinEvent;
import io.sedu.mc.parties.api.helper.PartyAPI;
import io.sedu.mc.parties.api.helper.PlayerAPI;
import io.sedu.mc.parties.api.mod.openpac.PACCompatManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class PartyHelper {

    public static boolean invitePlayerForced(UUID initiator, UUID futureMember) {
        if (!verifyRequest(initiator, futureMember))
            return false;

        //Open-PAC Support
        if (ServerConfigData.isPartySyncEnabled()) {
            return PACCompatManager.getHandler().addPartyMember(initiator, futureMember, false);
        }

        //This checks if initiator is in a party. Creates one if not.
        ServerPlayerData pD;
        if ((pD = PlayerAPI.getNormalPlayer(initiator)) != null && !pD.hasParty()) {
            new PartyData(initiator);
            //Party join event if player is online
            if (pD.getPlayer() != null)
                MinecraftForge.EVENT_BUS.post(new PartyJoinEvent(pD.getPlayer(), pD.getPartyId()));
        }
        return addPlayerToParty(futureMember, Objects.requireNonNull(PartyAPI.getPartyFromMember(initiator)));
    }

    public static boolean acceptInvite(UUID initiator, UUID futureMember) {
        ServerPlayerData fM;
        if ((fM = PlayerAPI.getNormalPlayer(futureMember)) == null) return false;
        if (fM.isInviter(initiator)) {
            fM.removeInviter(initiator);
            return invitePlayerForced(initiator, futureMember);
        }

        return false;
    }

    public static boolean acceptInvite(UUID futureMember) {
        AtomicBoolean ret = new AtomicBoolean(false);
        PlayerAPI.getPlayer(futureMember, playerData -> playerData.ifInviterExists((inviter) -> ret.set(acceptInvite(inviter, futureMember))));
        return ret.get();
    }

    public static boolean declineInvite(UUID initiator, UUID futureMember) {
        ServerPlayerData pD;
        if ((pD = PlayerAPI.getNormalPlayer(futureMember)) == null) return false;
        if (!pD.isInviter(initiator)) return false;
        ServerPlayer p;
        if ((p = PlayerAPI.getNormalServerPlayer(initiator)) != null) {
            p.sendMessage(new TranslatableComponent("messages.sedparties.phandler.invitedeclined", PlayerAPI.getName(futureMember)).withStyle(ChatFormatting.DARK_AQUA), initiator);
        }
        if ((p = PlayerAPI.getNormalServerPlayer(futureMember)) != null) {
            p.sendMessage(new TranslatableComponent("messages.sedparties.phandler.declineinvite", PlayerAPI.getName(initiator)).withStyle(ChatFormatting.DARK_AQUA), futureMember);
        }
        PlayerAPI.getPlayer(futureMember, playerData -> playerData.removeInviter(initiator));
        return true;
    }

    public static boolean declineInvite(UUID futureMember) {
        AtomicBoolean ret = new AtomicBoolean(false);
        PlayerAPI.getPlayer(futureMember, playerData -> playerData.ifInviterExists((inviter) -> ret.set(declineInvite(inviter, futureMember))));
        return ret.get();
    }

    //This adds the player to the given party.
    public static boolean addPlayerToParty(UUID futureMember, PartyData currentParty) {
        currentParty.addMember(futureMember);
        return true;
    }

    //Kicks player from party.
    public static boolean kickPlayer(UUID initiator, UUID removedMember) {
        if (!PartyAPI.inSameParty(initiator, removedMember)) {
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
        Objects.requireNonNull(PartyAPI.getPartyFromMember(removedMember)).removeMember(removedMember, wasKicked);
        return true;
    }

    public static boolean leaveParty(UUID memberLeaving) {
        ServerPlayerData p;
        if ((p = PlayerAPI.getNormalPlayer(memberLeaving)) != null && p.hasParty()) {
            //Open-PAC Support
            if (ServerConfigData.isPartySyncEnabled()) {
                return PACCompatManager.getHandler().partyMemberLeft(memberLeaving, false);
            }
            Objects.requireNonNull(PartyAPI.getPartyFromMember(memberLeaving)).removeMember(memberLeaving, false);
            return true;
        }
        return false;
    }

    public static boolean giveLeader(UUID newLeader) {
        //Open-PAC Support
        if (ServerConfigData.isPartySyncEnabled()) {
            return PACCompatManager.getHandler().changePartyLeader(newLeader, false);
        }
        Objects.requireNonNull(PartyAPI.getPartyFromMember(newLeader)).updateLeader(newLeader);
        return true;
    }

    public static void questionPlayer(UUID initiator, UUID futureMember) {
        //This checks if futureMember is a valid player that exists on the server.
        ServerPlayerData fM;
        if (verifyRequest(initiator, futureMember) && !((Objects.requireNonNull(fM = PlayerAPI.getNormalPlayer(futureMember))).isInviter(initiator))) {
            
            fM.addInviter(initiator);
            //Sends message to futureMember.
            fM.getPlayer().sendMessage(
                    new TranslatableComponent("messages.sedparties.phandler.receivedinvite", PlayerAPI.getName(initiator)).withStyle(ChatFormatting.DARK_AQUA)
                    , futureMember);

            fM.getPlayer().sendMessage(
                    new TranslatableComponent("messages.sedparties.phandler.receivedinvite2").withStyle(ChatFormatting.DARK_AQUA)
                                                                                 .append(new TranslatableComponent("messages.sedparties.phandler.receivedinvite3").withStyle(
                            style -> style.withColor(ChatFormatting.GREEN)
                                          .withUnderlined(true)
                                          .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                         "/party accept " + PlayerAPI.getName(initiator)))
                                          .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                         new TranslatableComponent("messages.sedparties.phandler.accepttooltip"))))
                    ).append(" "
                    ).append(
                            new TranslatableComponent("messages.sedparties.phandler.receivedinvite4").withStyle(
                                    style -> style.withColor(ChatFormatting.RED)
                                                  .withUnderlined(true)
                                                  .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                                 "/party decline " + PlayerAPI.getName(initiator)))
                                                  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                                 new TranslatableComponent("messages.sedparties.phandler.declinetooltip")))
                            )
                    )
                    , futureMember);

            //TODO: Support changing /party to /p, etc
            PlayerAPI.getServerPlayer(futureMember, serverPlayer -> serverPlayer.sendMessage(
                    new TranslatableComponent("messages.sedparties.phandler.receivedinvite5").withStyle(ChatFormatting.DARK_AQUA)
                                                                                             .append(new TextComponent("/party accept").withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)))
                                                                                             .append(new TextComponent(" | ").withStyle(ChatFormatting.DARK_AQUA))
                                                                                             .append(new TextComponent("/party decline").withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(true)))
                    , futureMember));

            PlayerAPI.getServerPlayer(initiator, serverPlayer -> serverPlayer.sendMessage(new TranslatableComponent("messages.sedparties.phandler.sendinvite",
                                                                                                                    PlayerAPI.getName(futureMember)).withStyle(ChatFormatting.DARK_AQUA), initiator));
        }


    }

    private static boolean verifyRequest(UUID initiator, UUID futureMember) {
        ServerPlayer p;
        if (PlayerAPI.getNormalPlayer(futureMember) == null || (p = PlayerAPI.getNormalServerPlayer(initiator)) == null || initiator.equals(futureMember)) {
            
            return false;
        }
        
        //This checks if the target is currently in a party.
        if (Objects.requireNonNull(PlayerAPI.getNormalPlayer(futureMember)).hasParty()) {
            p.sendMessage(new TranslatableComponent(
                    "messages.sedparties.phandler.alreadyhasparty", PlayerAPI.getName(futureMember)).withStyle(ChatFormatting.DARK_AQUA), initiator);
            return false;
        }
        if (Objects.requireNonNull(PlayerAPI.getNormalPlayer(initiator)).hasParty()) {
            if (!PartyAPI.isLeader(initiator)) {
                p.sendMessage(new TranslatableComponent(
                        "messages.sedparties.phandler.noinviteperms").withStyle(ChatFormatting.DARK_AQUA), initiator);
                return false;
            }
            if (Objects.requireNonNull(PartyAPI.getPartyFromMember(initiator)).isFull()) {
                p.sendMessage(new TranslatableComponent(
                        "messages.sedparties.phandler.partyfull").withStyle(ChatFormatting.DARK_AQUA), initiator);
                return false;
            }
        }
        return true;
    }

    public static void dismissInvite(UUID initiator) {
        PlayerAPI.getServerPlayer(initiator, serverPlayer -> serverPlayer.sendMessage(new TranslatableComponent(
                "messages.sedparties.phandler.declineinviteauto").withStyle(ChatFormatting.DARK_AQUA), initiator));
    }

    public static void dismissInvite(ServerPlayerData serverPlayerData, UUID initiator) {
        ServerPlayer p;
        PlayerAPI.getServerPlayer(initiator, serverPlayer -> serverPlayer.sendMessage(new TranslatableComponent(
                "messages.sedparties.phandler.declineinviteauto2", serverPlayerData.getName()).withStyle(ChatFormatting.DARK_AQUA), initiator));

        if ((p = serverPlayerData.getPlayer()) != null) {
            p.sendMessage(new TranslatableComponent("messages.sedparties.phandler.declineinviteauto3", PlayerAPI.getName(initiator)).withStyle(ChatFormatting.DARK_AQUA), initiator);
        }
    }
}
