package org.notleksa.autismcore.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.ReviveHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReviveCommands implements CommandExecutor {

    private final AutismCore plugin;
    private final Random random = new Random();

    public ReviveCommands(AutismCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player executor)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // no idea what kind of drugs sushi was on when she made this but if it works it works

        if (command.getName().equalsIgnoreCase("revive")) {

            if (!executor.hasPermission("autismcore.revive")) {
                executor.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
                return true;
            }

            if (args.length != 1) {
                executor.sendMessage(Component.text("Usage: /revive <player|all|random>", NamedTextColor.RED));
                return true;
            }

            ReviveHandler reviveHandler = plugin.getReviveHandler();
            if (reviveHandler == null) {
                sender.sendMessage(Component.text("Revive system isnt loaded???", NamedTextColor.RED));
                return true;
            }

            String targetArg = args[0].toLowerCase();

            switch (targetArg) {

                case "all" -> {
                    int revivedCount = 0;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!reviveHandler.isAlive(player)) {
                            player.teleport(executor.getLocation());
                            reviveHandler.setAlive(player);
                            player.sendMessage(Component.text("You have been revived by " + executor.getName(), NamedTextColor.GREEN));
                            revivedCount++;
                        }
                    }
                    executor.sendMessage(Component.text("Revived " + revivedCount + " players.", NamedTextColor.GREEN));
                }

                case "random" -> {
                    List<Player> deadPlayers = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!reviveHandler.isAlive(player)) deadPlayers.add(player);
                    }

                    if (deadPlayers.isEmpty()) {
                        executor.sendMessage(Component.text("There are no dead players to revive idiot", NamedTextColor.YELLOW));
                        return true;
                    }

                    Player target = deadPlayers.get(random.nextInt(deadPlayers.size()));
                    target.teleport(executor.getLocation());
                    reviveHandler.setAlive(target);
                    executor.sendMessage(Component.text("Revived " + target.getName(), NamedTextColor.GREEN));
                    target.sendMessage(Component.text("You have been revived by " + executor.getName(), NamedTextColor.GREEN));
                }

                default -> {
                    Player target = Bukkit.getPlayer(targetArg);
                    if (target == null) {
                        executor.sendMessage(Component.text("his ass is NOT a player", NamedTextColor.RED));
                        return true;
                    }

                    target.teleport(executor.getLocation());
                    reviveHandler.setAlive(target);
                    executor.sendMessage(Component.text("You revived " + target.getName(), NamedTextColor.GREEN));
                    target.sendMessage(Component.text("You have been revived by " + executor.getName(), NamedTextColor.GREEN));
                }
            }

            return true;
        }

        if (command.getName().equalsIgnoreCase("unrevive")) {

            if (!executor.hasPermission("autismcore.revive")) {
                executor.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
                return true;
            }

            if (args.length == 0) {
                executor.sendMessage(Component.text("Usage: /unrevive <player>", NamedTextColor.YELLOW));
                return true;
            }

            ReviveHandler reviveHandler = plugin.getReviveHandler();
            if (reviveHandler == null) {
                sender.sendMessage(Component.text("Revive system isnt loaded???", NamedTextColor.RED));
                return true;
            }

            String targetArg = args[0].toLowerCase();
            
            Player target = Bukkit.getPlayer(targetArg);
            if (target == null) {
                executor.sendMessage(Component.text("his ass is NOT a player", NamedTextColor.RED));
                return true;
            }

            reviveHandler.markDead(target);

            return true;
        }

        return false; // if command not recognized
    }
}
