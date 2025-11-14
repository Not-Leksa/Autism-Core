package org.notleksa.autismcore.commands;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.ReviveHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class WarpCommands implements CommandExecutor {

    private final AutismCore plugin;
    private final Map<String, Location> warps = new HashMap<>();
    private final Map<UUID, String> confirmWarp = new HashMap<>();
    private File warpsFile;
    private FileConfiguration warpsConfig;

    public WarpCommands(AutismCore plugin) {
        this.plugin = plugin;
        createWarpsFile();
        loadWarps();
    }

    // create warps.yml if it already doesnt exist
    private void createWarpsFile() {
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        if (!warpsFile.exists()) {
            warpsFile.getParentFile().mkdirs();
            try {
                warpsFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[AutismCore] Could not create warps.yml!");
                e.printStackTrace();
            }
        }
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
    }

    // load warps from warp.yml
    private void loadWarps() {
        if (warpsConfig.getConfigurationSection("warps") == null) return;

        for (String name : warpsConfig.getConfigurationSection("warps").getKeys(false)) {
            String world = warpsConfig.getString("warps." + name + ".world");
            double x = warpsConfig.getDouble("warps." + name + ".x");
            double y = warpsConfig.getDouble("warps." + name + ".y");
            double z = warpsConfig.getDouble("warps." + name + ".z");
            float yaw = (float) warpsConfig.getDouble("warps." + name + ".yaw");
            float pitch = (float) warpsConfig.getDouble("warps." + name + ".pitch");

            if (Bukkit.getWorld(world) != null) {
                warps.put(name.toLowerCase(), new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
            } else {
                Bukkit.getLogger().warning("[AutismCore] Warp '" + name + "' has invalid world '" + world + "'. Skipping.");
            }
        }

        Bukkit.getLogger().info("[AutismCore] Loaded " + warps.size() + " warps from warps.yml");
    }

    // save warps to warps.yml
    private void saveWarps() {
        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            Location loc = entry.getValue();
            String path = "warps." + entry.getKey();
            warpsConfig.set(path + ".world", loc.getWorld().getName());
            warpsConfig.set(path + ".x", loc.getX());
            warpsConfig.set(path + ".y", loc.getY());
            warpsConfig.set(path + ".z", loc.getZ());
            warpsConfig.set(path + ".yaw", loc.getYaw());
            warpsConfig.set(path + ".pitch", loc.getPitch());
        }

        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[AutismCore] Could not save warps.yml!");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String cmdName = command.getName().toLowerCase();

        switch (cmdName) {

            case "setwarp" -> {
                if (!player.hasPermission("autismcore.setwarp")) {
                    player.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
                    return true;
                }

                if (args.length != 1) {
                    player.sendMessage(Component.text("Usage: /setwarp <warp name>", NamedTextColor.RED));
                    return true;
                }

                String warpName = args[0].toLowerCase();
                warps.put(warpName, player.getLocation());
                saveWarps();
                player.sendMessage(Component.text("Warp '" + warpName + "' set successfully!", NamedTextColor.GREEN));

                return true;
            }

            case "warp" -> {
                if (args.length != 1) {
                    player.sendMessage(Component.text("Usage: /warp <warp name>", NamedTextColor.RED));
                    return true;
                }

                String warpName = args[0].toLowerCase();

                warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
                if (warpsConfig.getConfigurationSection("warps") == null ||
                    !warpsConfig.getConfigurationSection("warps").getKeys(false).contains(warpName)) {
                    player.sendMessage(Component.text("Warp '" + warpName + "' does not exist.", NamedTextColor.RED));
                    return true;
                }

                String world = warpsConfig.getString("warps." + warpName + ".world");
                double x = warpsConfig.getDouble("warps." + warpName + ".x");
                double y = warpsConfig.getDouble("warps." + warpName + ".y");
                double z = warpsConfig.getDouble("warps." + warpName + ".z");
                float yaw = (float) warpsConfig.getDouble("warps." + warpName + ".yaw");
                float pitch = (float) warpsConfig.getDouble("warps." + warpName + ".pitch");

                if (Bukkit.getWorld(world) == null) {
                    player.sendMessage(Component.text("Warp '" + warpName + "' has an invalid world.", NamedTextColor.RED));
                    return true;
                }

                Location warpLocation = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

                // warn if alive
                ReviveHandler reviveHandler = plugin.getReviveHandler();
                boolean isAlive = reviveHandler != null && reviveHandler.isAlive(player);

                if (isAlive) {
                    if (confirmWarp.containsKey(player.getUniqueId()) &&
                        confirmWarp.get(player.getUniqueId()).equals(warpName)) {
                        confirmWarp.remove(player.getUniqueId());
                        player.teleport(warpLocation);
                        player.sendMessage(Component.text("Teleported to warp '" + warpName + "'.", NamedTextColor.GREEN));
                        return true;
                    } else {
                        confirmWarp.put(player.getUniqueId(), warpName);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
                        player.sendMessage(Component.text("You are currently alive! Type the command again to confirm teleport.", NamedTextColor.RED));
                        return true;
                    }
                }

                player.teleport(warpLocation);
                player.sendMessage(Component.text("Teleported to warp '" + warpName + "'.", NamedTextColor.GREEN));

                return true;
            }

            case "listwarps" -> {
                warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);

                if (warpsConfig.getConfigurationSection("warps") == null ||
                    warpsConfig.getConfigurationSection("warps").getKeys(false).isEmpty()) {
                    player.sendMessage(Component.text("No warps have been set yet.", NamedTextColor.YELLOW));
                    return true;
                }

                Component message = Component.text("Available warps: ", NamedTextColor.GOLD);
                boolean first = true;

                for (String warpName : warpsConfig.getConfigurationSection("warps").getKeys(false)) {
                    if (!first) message = message.append(Component.text(", ", NamedTextColor.GRAY));
                    message = message.append(Component.text(warpName, NamedTextColor.AQUA));
                    first = false;
                }

                player.sendMessage(message);
                
                return true;
            }

            case "removewarp" -> {

            if (!player.hasPermission("autismcore.setwarp")) {
                player.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(Component.text("Usage: /removewarp <warp name>", NamedTextColor.RED));
                return true;
            }

            String warpName = args[0].toLowerCase();

            warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);

            if (warpsConfig.getConfigurationSection("warps") == null ||
                !warpsConfig.getConfigurationSection("warps").getKeys(false).contains(warpName)) {

                player.sendMessage(Component.text("Warp '" + warpName + "' does not exist.", NamedTextColor.RED));
                return true;
            }

            warpsConfig.set("warps." + warpName, null);

            try {
                warpsConfig.save(warpsFile);
            } catch (IOException e) {
                player.sendMessage(Component.text("Error saving warps.yml!", NamedTextColor.RED));
                e.printStackTrace();
                return true;
            }

            player.sendMessage(Component.text("Warp '" + warpName + "' has been removed.", NamedTextColor.GREEN));
            return true;
        }


        }

        return false;
    }
}
