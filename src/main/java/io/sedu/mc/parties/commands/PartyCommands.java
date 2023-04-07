package io.sedu.mc.parties.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import io.sedu.mc.parties.data.PartyHelper;
import io.sedu.mc.parties.network.ClientPacketData;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.UUID;

import static io.sedu.mc.parties.data.Util.hasParty;
import static io.sedu.mc.parties.data.Util.isLeader;

public class PartyCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("party")
            .then(Commands.literal("invite")
                .then(Commands.argument("player", new NotSelfArgument(false))
                    .executes(ctx -> {
                        PartyHelper.questionPlayer(ctx.getSource().getPlayerOrException().getUUID(), EntityArgument.getPlayer(ctx, "player").getUUID());
                        return Command.SINGLE_SUCCESS;})
                )
            ).then(Commands.literal("accept").then(Commands.argument("initiator", new NotSelfArgument(false)).executes(ctx -> {
                if (PartyHelper.acceptInvite(EntityArgument.getPlayer(ctx, "initiator").getUUID(), ctx.getSource().getPlayerOrException().getUUID())) {
                    
                    return Command.SINGLE_SUCCESS;
                }
                else {
                    return 0;
                }
            })))
            .then(Commands.literal("accept").executes(ctx -> {
                if (PartyHelper.acceptInvite(ctx.getSource().getPlayerOrException().getUUID())) {

                    return Command.SINGLE_SUCCESS;
                }
                else {
                    return 0;
                }
            }))
            .then(Commands.literal("decline").then(Commands.argument("initiator", new NotSelfArgument(false)).executes(ctx -> {
                if (PartyHelper.declineInvite(EntityArgument.getPlayer(ctx, "initiator").getUUID(), ctx.getSource().getPlayerOrException().getUUID())) {
                    return Command.SINGLE_SUCCESS;
                }
                else {
                    return 0;
                }
            })))
            .then(Commands.literal("decline").executes(ctx -> {
                if (PartyHelper.declineInvite(ctx.getSource().getPlayerOrException().getUUID())) {
                    return Command.SINGLE_SUCCESS;
                }
                else {
                    return 0;
                }
            }))
            .then(Commands.literal("reload").executes(ctx -> {
                PartiesPacketHandler.sendToPlayer(new ClientPacketData(7), ctx.getSource().getPlayerOrException());
                return Command.SINGLE_SUCCESS;
            }))
            .then(Commands.literal("kick").then(Commands.argument("member", new NotSelfArgument(true)).executes(ctx -> {
                UUID senderId;
                if (!hasParty(senderId = ctx.getSource().getPlayerOrException().getUUID())) {
                    return 0;
                }

                if (!isLeader(senderId)) {
                    ctx.getSource().getPlayerOrException().sendMessage(new TranslatableComponent("messages.sedparties.command.kickfail").withStyle(ChatFormatting.DARK_AQUA), ctx.getSource().getPlayerOrException().getUUID());
                    return 0;
                }
                return PartyHelper.kickPlayer(ctx.getSource().getPlayerOrException().getUUID(), NotSelfArgument.getPlayerUUID(ctx, "member", senderId)) ? Command.SINGLE_SUCCESS : 0;
            })))
            .then(Commands.literal("leave")
                .executes(ctx -> {
                  if (PartyHelper.leaveParty(ctx.getSource().getPlayerOrException().getUUID())) {
                      
                      return Command.SINGLE_SUCCESS;
                  } else {
                      
                      return 0;
                  }
                }))
            .then(Commands.literal("leader")
                .then(Commands.argument("member", new NotSelfArgument(true))
                    .executes(ctx -> {
                        if (isLeader(ctx.getSource().getPlayerOrException().getUUID()) &&
                                PartyHelper.giveLeader(EntityArgument.getPlayer(ctx, "member").getUUID())) {
                            
                            return Command.SINGLE_SUCCESS;
                        } else {
                            
                            return 0;
                        }
                    })
                )
            )
        );
    }
}
