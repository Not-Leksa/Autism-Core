package org.notleksa.autismcore.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ScoreboardHandler {

    private final Plugin plugin;
    private final ReviveHandler reviveHandler;

    public ScoreboardHandler(Plugin plugin, ReviveHandler reviveHandler) {
        this.plugin = plugin;
        this.reviveHandler = reviveHandler;
    }

    public void updateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective(
                "serverInfo",
                "dummy",
                Component.text("Server Info", NamedTextColor.GOLD)
        );

        // load from config.yml
        String serverName = plugin.getConfig().getString("server.name", "Unknown");
        String serverIP = plugin.getConfig().getString("server.ip", "Unknown");
        String eventName = plugin.getConfig().getString("event.name", "Unknown");

        long aliveCount = Bukkit.getOnlinePlayers().stream().filter(reviveHandler::isAlive).count();
        long deadCount = Bukkit.getOnlinePlayers().size() - aliveCount;
        int onlineCount = Bukkit.getOnlinePlayers().size();
        int tokens = reviveHandler.getReviveTokens(player);

        Component message = Component.text(serverName).decorate(TextDecoration.BOLD)
            .append(Component.text(eventName)).decorate(TextDecoration.BOLD)
            .append(Component.text(aliveCount))
            .append(Component.text(deadCount))
            .append(Component.text(onlineCount))
            .append(Component.text(tokens))
            .append(Component.text(serverIP));

        player.setScoreboard(board);
    }

    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    public void startUpdating() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::updateAllScoreboards, 0L, 20L);
    }
}
