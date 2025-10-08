package org.notleksa.autismcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class RevTokenCommands implements CommandExecutor {

    private final AutismCore plugin; 
    private final ReviveHandler reviveHandler;

    public RevTokenCommands(AutismCore plugin, ReviveHandler reviveHandler) {
        this.plugin = plugin;
        this.reviveHandler = reviveHandler;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String cmdName = command.getName().toLowerCase();

        switch (cmdName) {

            case "userevive" -> {
                if (reviveHandler.isAlive(player)) {
                    player.sendMessage(Component.text("You're already alive.", NamedTextColor.RED));
                    return true;
                }

                if (!reviveHandler.useReviveToken(player)) {
                    player.sendMessage(Component.text("You don't have any revive tokens.", NamedTextColor.RED));
                    return true;
                }

                if (reviveHandler.hasPendingRevive(player)) {
                    player.sendMessage(Component.text("You already have a revive request pending.", NamedTextColor.YELLOW));
                    return true;
                }

                reviveHandler.addReviveRequest(player);
                player.sendMessage(Component.text("Revive request sent. Waiting for staff approval...", NamedTextColor.GREEN));

                // Notify staff
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.hasPermission("autismcore.revive")) {
                        online.sendMessage(Component.text("[Revive Request] " + player.getName() + " wants to be revived! Use /reviveaccept " + player.getName(), NamedTextColor.GOLD));
                    }
                }
            }

            case "reviveaccept" -> {
                if (!sender.hasPermission("autismcore.revive")) {
                    sender.sendMessage(Component.text("You don't have permission to accept revives.", NamedTextColor.RED));
                    return true;
                }

                if (args.length != 1) {
                    sender.sendMessage(Component.text("Usage: /reviveaccept <player>", NamedTextColor.RED));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
                    return true;
                }

                if (!reviveHandler.hasPendingRevive(target)) {
                    sender.sendMessage(Component.text("That player doesn't have a pending revive request.", NamedTextColor.YELLOW));
                    return true;
                }

                reviveHandler.removeReviveRequest(target);
                reviveHandler.setAlive(target, true);

                target.teleport(player.getLocation());

                target.sendMessage(Component.text("You have been revived by " + player.getName() + "!", NamedTextColor.GREEN));
                sender.sendMessage(Component.text("You revived " + target.getName(), NamedTextColor.GREEN));
            }

            case "addrevive" -> {
                if (!sender.hasPermission("autismcore.addrevive")) {
                    sender.sendMessage(Component.text("You don't have permission.", NamedTextColor.RED));
                    return true;
                }

                if (args.length != 2) {
                    sender.sendMessage(Component.text("Usage: /addrevive <player> <amount>", NamedTextColor.RED));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Component.text("Enter a valid number.", NamedTextColor.RED));
                    return true;
                }

                reviveHandler.addReviveTokens(target, amount);
                sender.sendMessage(Component.text("Gave " + amount + " revive token(s) to " + target.getName(), NamedTextColor.GREEN));
                target.sendMessage(Component.text("You received " + amount + " revive token(s)!", NamedTextColor.GOLD));
            }

            default -> sender.sendMessage(Component.text("Unknown command.", NamedTextColor.RED));
        }

        return true;
    }
}
