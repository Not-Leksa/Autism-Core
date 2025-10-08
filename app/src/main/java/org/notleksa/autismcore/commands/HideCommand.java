package org.notleksa.autismcore.commands;

import org.notleksa.autismcore.AutismCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideCommand implements CommandExecutor {

    private final AutismCore plugin;

    public HideCommand(AutismCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /hide <all|staff|host|off>");
            return true;
        }

        String type = args[0].toLowerCase();

        switch (type) {
            case "all" -> {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(player)) {
                        player.hidePlayer(plugin, other);
                    }
                }
            }
            case "staff" -> {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(player) && other.hasPermission("autismcore.staff")) {
                        player.hidePlayer(plugin, other);
                    }
                }
            }
            case "host" -> {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(player) && other.hasPermission("autismcore.host")) {
                        player.hidePlayer(plugin, other);
                    }
                }
            }
            case "off" -> {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(player)) {
                        player.showPlayer(plugin, other);
                    }
                }
            }

            default -> player.sendMessage("idiot thats not a fucking option. Usage: /hide <all|staff|host|off>");
        }

        return true;
    }
}
