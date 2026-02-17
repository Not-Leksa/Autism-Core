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
            sender.sendMessage(Component.text("Usage: /sudo <player> <message | /command>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            return true;
        }

        // Combine everything after player
        String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // If starts with "/", treat as command
        if (input.startsWith("/")) {

            String cmd = input.substring(1); 

            String base = cmd.split(" ")[0].toLowerCase();
            if (base.equals("op") || base.equals("deop")) {
                sender.sendMessage("§cno");
                return true;
            }

            boolean success = Bukkit.dispatchCommand(target, cmd);

            if (!success) {
                sender.sendMessage(Component.text("That command does not exist or failed.", NamedTextColor.RED));
                return true;
            }

            return true;
        }

        target.chat(input);

        return true;
    }
}
