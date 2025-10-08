package org.notleksa;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public final class AutismCore extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("AutismCore has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("AutismCore has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to the server, " + event.getPlayer().getName() + "!");
    }
}