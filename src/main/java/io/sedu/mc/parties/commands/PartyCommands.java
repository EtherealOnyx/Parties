package io.sedu.mc.parties.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import io.sedu.mc.parties.data.PartyHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.UUID;

import static io.sedu.mc.parties.data.Util.isLeader;

public class PartyCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("party")
            .then(Commands.literal("invite")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> {
                        if (PartyHelper.invitePlayer(ctx.getSource().getPlayerOrException().getUUID(),
                                EntityArgument.getPlayer(ctx, "player").getUUID())) {
                            System.out.println("Party creation success.");
                            return Command.SINGLE_SUCCESS;
                        }
                        else {
                            System.out.println("Party creation failed!");
                            return 0;
                        }
                    }
                    )
                )
            )
            .then(Commands.literal("kick")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> {
                        if (isLeader(ctx.getSource().getPlayerOrException().getUUID()) &&
                                PartyHelper.kickPlayer(ctx.getSource().getPlayerOrException().getUUID(),
                                        EntityArgument.getPlayer(ctx, "player").getUUID())) {
                            System.out.println("Player kick successful.");
                            return Command.SINGLE_SUCCESS;
                        }
                        else {
                            System.out.println("Player kick failed!");
                            return 0;
                        }
                    })
                )
            )
            .then(Commands.literal("leave")
                .executes(ctx -> {
                  if (PartyHelper.leaveParty(ctx.getSource().getPlayerOrException().getUUID())) {
                      System.out.println("Player left party successfully.");
                      return Command.SINGLE_SUCCESS;
                  } else {
                      System.out.println("Player leaving failed!");
                      return 0;
                  }
                })
            )
            .then(Commands.literal("leader")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> {
                        if (isLeader(ctx.getSource().getPlayerOrException().getUUID()) &&
                                PartyHelper.giveLeader(EntityArgument.getPlayer(ctx, "player").getUUID())) {
                            System.out.println("Party leader changed successfully.");
                            return Command.SINGLE_SUCCESS;
                        } else {
                            System.out.println("Party leader change failed!");
                            return 0;
                        }
                    })
                )
            )
        );
    }
}
