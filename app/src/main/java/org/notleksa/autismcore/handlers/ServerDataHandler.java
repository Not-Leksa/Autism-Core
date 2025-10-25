package org.notleksa.autismcore.handlers;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.notleksa.autismcore.AutismCore;

public class ServerDataHandler implements Listener {

    // NO LIKE OH MY GOD THIS CODE FUCKING SUCKS LMAO

    private final AutismCore plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public ServerDataHandler(AutismCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml!");
                e.printStackTrace();
            }
        }

        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (dataConfig.getConfigurationSection("players." + player.getUniqueId()) == null) {
            plugin.getLogger().info("Creating new data section for " + player.getName());
            setPlayerData(player, "revive_tokens", 0);
        }

        int tokens = getReviveTokens(player);
        plugin.getLogger().info("Loaded data for " + player.getName() + " (Revive Tokens: " + tokens + ")");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        saveDataFile();
        plugin.getLogger().info("Saved data for " + player.getName());
    }

    // file management (thanks sushi :3)

    public void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml!");
            e.printStackTrace();
        }
    }

    public void reloadData() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    // player data

    public void setPlayerData(Player player, String key, Object value) {
        UUID uuid = player.getUniqueId();
        dataConfig.set("players." + uuid + "." + key, value);
        saveDataFile();
    }

    public Object getPlayerData(Player player, String key) {
        UUID uuid = player.getUniqueId();
        return dataConfig.get("players." + uuid + "." + key);
    }

    public int getPlayerInt(Player player, String key, int defaultValue) {
        UUID uuid = player.getUniqueId();
        return dataConfig.getInt("players." + uuid + "." + key, defaultValue);
    }

    public String getPlayerString(Player player, String key, String defaultValue) {
        UUID uuid = player.getUniqueId();
        return dataConfig.getString("players." + uuid + "." + key, defaultValue);
    }

    public boolean getPlayerBoolean(Player player, String key, boolean defaultValue) {
        UUID uuid = player.getUniqueId();
        return dataConfig.getBoolean("players." + uuid + "." + key, defaultValue);
    }

    public void incrementPlayerInt(Player player, String key, int amount) {
        int current = getPlayerInt(player, key, 0);
        setPlayerData(player, key, current + amount);
    }

    public void removePlayerData(Player player, String key) {
        UUID uuid = player.getUniqueId();
        dataConfig.set("players." + uuid + "." + key, null);
        saveDataFile();
    }

    public void clearPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        dataConfig.set("players." + uuid, null);
        saveDataFile();
    }

    public void clearPlayerData(UUID uuid) {
        dataConfig.set("players." + uuid, null);
        saveDataFile();
    }

    public void setReviveTokens(Player player, int tokens) {
        setPlayerData(player, "revive_tokens", tokens);
    }

    public int getReviveTokens(Player player) {
        return getPlayerInt(player, "revive_tokens", 0);
    }

    public void addReviveTokens(Player player, int amount) {
        incrementPlayerInt(player, "revive_tokens", amount);
    }

    // server data

    public void setServerData(String section, String key, Object value) {
        dataConfig.set("server." + section + "." + key, value);
        saveDataFile();
    }

    public int getServerInt(String section, String key) {
        return dataConfig.getInt("server." + section + "." + key, 0);
    }

    // save data

    public void saveAll() {
        saveDataFile();
    }
}
