package org.notleksa.autismcore.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.CooldownHandler;
import org.notleksa.autismcore.handlers.ReviveHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class RevTokenCommands implements CommandExecutor {

    private final AutismCore plugin; 
    private final ReviveHandler reviveHandler;
    private final CooldownHandler cooldownHandler;
    private final Random random = new Random();

    public RevTokenCommands(AutismCore plugin, ReviveHandler reviveHandler, CooldownHandler cooldownHandler) {
        this.plugin = plugin;
        this.reviveHandler = reviveHandler;
        this.cooldownHandler = cooldownHandler;
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
                    player.sendMessage(Component.text("You're already alive fuh ahh idiot", NamedTextColor.RED));
                    return true;
                }

                if (!reviveHandler.useReviveToken(player)) {
                    player.sendMessage(Component.text("Bro you dont have no rev tokens broke bitch", NamedTextColor.RED));
                    return true;
                }

                if (reviveHandler.hasPendingRevive(player)) {
                    player.sendMessage(Component.text("Dumbass stop asking you fuh ahh pmo we gonna rev you maybe like jesus christ", NamedTextColor.YELLOW));
                    return true;
                }

                reviveHandler.addReviveRequest(player);
                player.sendMessage(Component.text("you sent the request type shi, waiting for staff to say yes", NamedTextColor.GREEN));

                // Notify staff
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.hasPermission("autismcore.revive")) {
                        online.sendMessage(Component.text("[Revive Request] " + player.getName() + " wants to be revived fr!!! Use /reviveaccept " + player.getName(), NamedTextColor.GOLD));
                    }
                }
            }

            case "reviveaccept" -> {
                if (!sender.hasPermission("autismcore.revive")) {
                    sender.sendMessage(Component.text("You don't have permission to accept revives dumbass", NamedTextColor.RED));
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
                    sender.sendMessage(Component.text("you dont have perms faggot", NamedTextColor.RED));
                    return true;
                }

                if (args.length != 2) {
                    sender.sendMessage(Component.text("Usage: /addrevive <player> <amount>", NamedTextColor.RED));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Component.text("his ass is NOT a player", NamedTextColor.RED));
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Component.text("his ass is NOT a number", NamedTextColor.RED));
                    return true;
                }

                reviveHandler.addReviveTokens(target, amount);
                sender.sendMessage(Component.text("Gave " + amount + " revive token(s) to " + target.getName(), NamedTextColor.GREEN));
                target.sendMessage(Component.text("You received " + amount + " revive token(s)!", NamedTextColor.GOLD));
            }

            case "tokens" -> {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Component.text("his ass is NOT a player", NamedTextColor.RED));
                    return true;
                }
                sender.sendMessage(Component.text(target.getName() + "got like about " + reviveHandler.getReviveTokens(target) + " on him", NamedTextColor.GREEN));
            }

            case "gamble" -> {
                int playerTokens = reviveHandler.getReviveTokens(player);
                String action = "tokengamble";
                if (cooldownHandler.isOnCooldown(player, action)) {
                    long secondsLeft = cooldownHandler.getTimeLeft(player, action);
                    player.sendMessage(Component.text("bro your on cooldown for like " + secondsLeft + " seconds", NamedTextColor.RED));
                    return true;
                }

                if (playerTokens < 1) {
                    player.sendMessage(Component.text("broke boy you aint have no tokens to gamble ", NamedTextColor.RED));
                    return true;
                }

                if (args.length < 1) {
                    player.sendMessage(Component.text("Usage: /gamble <amounth|all>", NamedTextColor.RED));
                    return true;
                }

                int gambleAmount;

                if (args[0].equalsIgnoreCase("all")) {
                    gambleAmount = playerTokens;
                } else {
                    try {
                    gambleAmount = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Component.text("enter a number idiot", NamedTextColor.RED));
                        return true;
                    }
                }

                if (gambleAmount <= 0) {
                    player.sendMessage(Component.text("how you gon gamble 0 tokens vro", NamedTextColor.RED));
                    return true;
                }

             if (gambleAmount > playerTokens) {
                    player.sendMessage(Component.text("you do NOT have that many tokens idiot", NamedTextColor.RED));
                    return true;
                }

                for (int i = 0; i < gambleAmount; i++) {
                    reviveHandler.useReviveToken(player);
                }

                boolean win = random.nextBoolean();
                if (win) {
                    int reward = gambleAmount * 2;
                    reviveHandler.addReviveTokens(player, reward);
                    player.sendMessage(Component.text("You won!!! you got like " + reward + " revive tokens fr!!!", NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("You lost like " + gambleAmount + " tokens bro tf is wrong with you", NamedTextColor.RED));
                }
            }

            default -> sender.sendMessage(Component.text("Unknown command.", NamedTextColor.RED));
        }
        return true;
    }
}
