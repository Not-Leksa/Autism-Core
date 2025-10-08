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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReviveCommand implements CommandExecutor {

    private final AutismCore plugin;
    private final ReviveHandler reviveHandler;
    private final Random random = new Random();

    public ReviveCommand(AutismCore plugin, ReviveHandler reviveHandler) {
        this.plugin = plugin;
        this.reviveHandler = reviveHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player executor)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Permission check
        if (!executor.hasPermission("autismcore.revive")) {
            executor.sendMessage(Component.text("You don't have permission to use this command faggot", NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            executor.sendMessage(Component.text("Usage: /revive <player|all|random>", NamedTextColor.RED));
            return true;
        }

        String targetArg = args[0].toLowerCase();

        switch (targetArg) {

            case "all" -> {
                int revivedCount = 0;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!reviveHandler.isAlive(player)) {
                        player.teleport(executor.getLocation());
                        reviveHandler.setAlive(player, true);
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
                    executor.sendMessage(Component.text("There are no dead players to revive.", NamedTextColor.YELLOW));
                    return true;
                }

                Player target = deadPlayers.get(random.nextInt(deadPlayers.size()));
                target.teleport(executor.getLocation());
                reviveHandler.setAlive(target, true);
                executor.sendMessage(Component.text("Revived " + target.getName(), NamedTextColor.GREEN));
                target.sendMessage(Component.text("You have been revived by " + executor.getName(), NamedTextColor.GREEN));
            }

            default -> {
                Player target = Bukkit.getPlayer(targetArg);
                if (target == null) {
                    executor.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
                    return true;
                }

                target.teleport(executor.getLocation());
                reviveHandler.setAlive(target, true);
                executor.sendMessage(Component.text("You revived " + target.getName(), NamedTextColor.GREEN));
                target.sendMessage(Component.text("You have been revived by " + executor.getName(), NamedTextColor.GREEN));
            }
        }

        return true;
    }
}
