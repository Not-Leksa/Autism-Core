package org.notleksa.autismcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.ReviveHandler;

public class TeleportCommands implements CommandExecutor {

    private final AutismCore plugin;
    private final ReviveHandler reviveHandler;

    public TeleportCommands(AutismCore plugin) {
        this.plugin = plugin;
        this.reviveHandler = plugin.getReviveHandler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        switch (command.getName().toLowerCase()) {
            case "tpalive" -> teleportAlive(player);
            case "tpdead" -> teleportDead(player);
            default -> {
                return false;
            }
        }

        return true;
    }

    private void teleportAlive(Player target) {
        int count = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (reviveHandler.isAlive(p)) {
                p.teleport(target.getLocation());
                count++;
            }
        }
        target.sendMessage("Teleported " + count + " alive players to you.");
    }

    private void teleportDead(Player target) {
        int count = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!reviveHandler.isAlive(p)) {
                p.teleport(target.getLocation());
                count++;
            }
        }
        target.sendMessage("Teleported " + count + " dead players to you.");
    }
}
