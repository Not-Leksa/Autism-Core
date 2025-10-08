package org.notleksa.autismcore.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;

public class SetSpawnCommand implements CommandExecutor {

    private final AutismCore plugin;

    public SetSpawnCommand(AutismCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("autismcore.setspawn")) {
            player.sendMessage("You don't have permission to use this command faggot");
            return true;
        }

        Location loc = player.getLocation();
        plugin.getConfig().set("spawn.world", loc.getWorld().getName());
        plugin.getConfig().set("spawn.x", loc.getX());
        plugin.getConfig().set("spawn.y", loc.getY());
        plugin.getConfig().set("spawn.z", loc.getZ());
        plugin.getConfig().set("spawn.yaw", loc.getYaw());
        plugin.getConfig().set("spawn.pitch", loc.getPitch());
        plugin.saveConfig();

        player.sendMessage("Spawn location set successfully.");
        return true;
    }
}
