package org.notleksa.autismcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;

public class SpawnCommand implements CommandExecutor {

    private final AutismCore plugin;

    public SpawnCommand(AutismCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        String worldName = plugin.getConfig().getString("spawn.world");
        double x = plugin.getConfig().getDouble("spawn.x");
        double y = plugin.getConfig().getDouble("spawn.y");
        double z = plugin.getConfig().getDouble("spawn.z");
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("theres no spawn the owner of this server might be stupid");
            return true;
        }

        Location spawnLoc = new Location(world, x, y, z, yaw, pitch);
        player.teleport(spawnLoc);
        return true;
    }
}
