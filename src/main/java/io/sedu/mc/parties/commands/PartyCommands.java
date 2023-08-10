package io.sedu.mc.parties.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import io.sedu.mc.parties.api.mod.gamestages.GSEventHandler;
import io.sedu.mc.parties.api.mod.gamestages.SyncType;
import io.sedu.mc.parties.data.PartyHelper;
import io.sedu.mc.parties.data.PartySaveData;
import io.sedu.mc.parties.data.ServerConfigData;
import io.sedu.mc.parties.network.ClientPacketData;
import io.sedu.mc.parties.network.PartiesPacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.command.EnumArgument;

import java.util.UUID;

import static io.sedu.mc.parties.api.helper.PartyAPI.hasParty;
import static io.sedu.mc.parties.api.helper.PartyAPI.isLeader;

public class PartyCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("applypreset").executes(ctx -> {
             PartiesPacketHandler.sendToPlayer(
                     new ClientPacketData(9),
                     ctx.getSource().getPlayerOrException());
             return Command.SINGLE_SUCCESS;
        }
        ));
        if (ModList.get().isLoaded("gamestages")) {
            dispatcher.register(Commands.literal("party").then(Commands.literal("sync")
               .then(Commands.argument("type", EnumArgument.enumArgument(SyncType.class)).executes(ctx -> {
                   ServerPlayer p = ctx.getSource().getPlayerOrException();
                   SyncType t = ctx.getArgument("type", SyncType.class);
                   if (GSEventHandler.changePlayerOption(p.getUUID(), t, false))
                       p.sendMessage(new TranslatableComponent("messages.sedparties.command.syncchange", t).withStyle(ChatFormatting.DARK_AQUA), ctx.getSource().getPlayerOrException().getUUID());
                   else
                       p.sendMessage(new TranslatableComponent("messages.sedparties.command.syncsame", t).withStyle(ChatFormatting.DARK_AQUA), ctx.getSource().getPlayerOrException().getUUID());
                   if (!GSEventHandler.validType(t))
                       p.sendMessage(new TranslatableComponent("messages.sedparties.command.syncoverride", ServerConfigData.syncGameStages.get(), t).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY), ctx.getSource().getPlayerOrException().getUUID());
                   return Command.SINGLE_SUCCESS;
            }))));
        }
        dispatcher.register(Commands.literal("party")
            .then(Commands.literal("invite")
                .then(Commands.argument("player", new NotSelfArgument(false))
                    .executes(ctx -> {
                        PartyHelper.questionPlayer(ctx.getSource().getPlayerOrException().getUUID(), EntityArgument.getPlayer(ctx, "player").getUUID());
                        return Command.SINGLE_SUCCESS;})
                )
            ).then(Commands.literal("accept").then(Commands.argument("initiator", new NotSelfArgument(false)).executes(ctx -> {
                if (PartyHelper.acceptInvite(EntityArgument.getPlayer(ctx, "initiator").getUUID(), ctx.getSource().getPlayerOrException().getUUID())) {
                    //Add Party Update;
                    PartySaveData.get().setDirty();
                    return Command.SINGLE_SUCCESS;
                }
                else {
                    return 0;
                }
            })))
            .then(Commands.literal("accept").executes(ctx -> {
                if (PartyHelper.acceptInvite(ctx.getSource().getPlayerOrException().getUUID())) {
                    //Add Party Update;
                    PartySaveData.get().setDirty();
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
                if (PartyHelper.kickPlayer(ctx.getSource().getPlayerOrException().getUUID(), NotSelfArgument.getPlayerUUID(ctx, "member", senderId))) {
                    //Add Party Update;
                    PartySaveData.get().setDirty();
                    return Command.SINGLE_SUCCESS;
                }
                return 0;
            })))
            .then(Commands.literal("leave").executes(ctx -> {
                if (PartyHelper.leaveParty(ctx.getSource().getPlayerOrException().getUUID())) {
                    //Add Party Update;
                    PartySaveData.get().setDirty();
                    return Command.SINGLE_SUCCESS;
                } else {

                    return 0;
                }}))
            .then(Commands.literal("leader")
                .then(Commands.argument("member", new NotSelfArgument(true))
                    .executes(ctx -> {
                        if (isLeader(ctx.getSource().getPlayerOrException().getUUID()) &&
                                PartyHelper.giveLeader(EntityArgument.getPlayer(ctx, "member").getUUID())) {
                            //Add Party Update;
                            PartySaveData.get().setDirty();
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
