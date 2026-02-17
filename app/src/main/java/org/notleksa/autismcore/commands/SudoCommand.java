package org.notleksa.autismcore.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SudoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("autismcore.sudo")) {
            sender.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /sudo <player> <message>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return true;
        }

        // for chat
        if (args[1].equalsIgnoreCase("chat")) {

            if (args.length < 3) {
                sender.sendMessage(Component.text("Usage: /sudo <player> <message>", NamedTextColor.RED));
                return true;
            }

            String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            target.chat(message);

            return true;
        }

        // for commands fr this prolly dont even work
        String cmd = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Bukkit.dispatchCommand(target, cmd);

        return true;
    }
}
