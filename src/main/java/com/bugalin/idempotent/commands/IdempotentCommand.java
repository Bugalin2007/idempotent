package com.bugalin.idempotent.commands;

import com.bugalin.idempotent.items.weapons.EWeapons;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class IdempotentCommand{

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("idempotent")
                .then(Commands.literal("talk")
                        .executes(IdempotentCommand::talk)
                )
                .then(Commands.literal("giveitem")
                        .then(Commands.argument("item",EnumArgument.of(EWeapons.class))
                                .executes(IdempotentCommand::giveitem)))
                .build();
    }

    private static int talk(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender(); // Retrieve the command sender
        Entity executor = ctx.getSource().getExecutor(); // Retrieve the command executor, which may or may not be the same as the sender

        if (!(executor instanceof Player player)) {
            sender.sendPlainMessage("You do not worth greeting!");
            return Command.SINGLE_SUCCESS;
        }

        player.sendPlainMessage("Hello World! From Idempotent Plugin!");
        return Command.SINGLE_SUCCESS;
    }

    private static int giveitem(CommandContext<CommandSourceStack> ctx) {
        EWeapons weapon = ctx.getArgument("item",EWeapons.class);
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player)) {
            sender.sendPlainMessage("You can't be given an item!");
            return Command.SINGLE_SUCCESS;
        }

        player.getInventory().addItem(weapon.getItemStack());
        player.sendPlainMessage("You have been given an item!");
        return Command.SINGLE_SUCCESS;

    }
}
