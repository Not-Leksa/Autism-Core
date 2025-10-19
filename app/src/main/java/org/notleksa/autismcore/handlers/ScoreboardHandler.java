package org.notleksa.autismcore.handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.notleksa.autismcore.AutismCore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ScoreboardHandler implements Listener {
    private final AutismCore plugin;
    private final ReviveHandler reviveHandler;
    private File scoreboardFile;
    private FileConfiguration scoreboardConfig;
    private final Map<Player, Scoreboard> playerBoards = new HashMap<>();
    private final MiniMessage mm = MiniMessage.miniMessage();
    private String eventMessage = "";
    private String eventTimerDisplay = "";

    public ScoreboardHandler(AutismCore plugin, ReviveHandler reviveHandler) {
        this.plugin = plugin;
        this.reviveHandler = reviveHandler;
        createScoreboardFile();
        startUpdater();
    }

    private void createScoreboardFile() {
        scoreboardFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        if (!scoreboardFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                scoreboardFile.createNewFile();
                scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
                scoreboardConfig.set("title", "&dAutismCore");
                scoreboardConfig.set("lines", List.of(
                        "&7--------------------",
                        "&fPlayer: &d%player%",
                        "&fOnline: &d%online%/&d%max%",
                        "&fRevives: &d%revivetokens%",
                        "&7--------------------"
                ));
                scoreboardConfig.save(scoreboardFile);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create scoreboard.yml: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent ev) {
        showScoreboard(ev.getPlayer());
    }

    public void showScoreboard(Player player) {
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        if (mgr == null) {
            plugin.getLogger().warning("ScoreboardManager is null, cannot show scoreboard.");
            return;
        }

        // Remove old
        playerBoards.remove(player);

        Scoreboard board = mgr.getNewScoreboard();

        String rawTitle = scoreboardConfig.getString("title", "");
        if (rawTitle == null || rawTitle.isBlank()) {
            plugin.getLogger().warning("Scoreboard title is blank or missing in scoreboard.yml");
        }
        Component compTitle = mm.deserialize(rawTitle);
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(compTitle);

        String objName = "sb_" + player.getUniqueId().toString().substring(0, 8);
        Objective obj = board.registerNewObjective(objName, "dummy", legacyTitle);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateLines(player, board, obj);

        player.setScoreboard(board);
        playerBoards.put(player, board);
    }

    private void startUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Scoreboard board = playerBoards.get(p);
                    if (board == null) {
                        showScoreboard(p);
                        continue;
                    }
                    Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
                    if (obj == null) {
                        showScoreboard(p);
                        continue;
                    }
                    updateLines(p, board, obj);
                }
            }
        }.runTaskTimer(plugin, 0L, 40L); // update every 2 seconds (40 ticks)
    }

    private void updateLines(Player player, Scoreboard board, Objective obj) {
        board.getEntries().forEach(board::resetScores);

        List<String> lines = scoreboardConfig.getStringList("lines");
        if (lines == null || lines.isEmpty()) {
            plugin.getLogger().warning("No lines configured in scoreboard.yml");
            return;
        }

        int score = lines.size();
        for (String raw : lines) {
            String parsed = raw
                .replace("%player%", player.getName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()))
                .replace("%revivetokens%", String.valueOf(plugin.getReviveHandler().getReviveTokens(player)))
                .replace("%alive%", String.valueOf(reviveHandler.getAliveCount()))
                .replace("%dead%", String.valueOf(reviveHandler.getDeadCount()))
                .replace("%event%", String.valueOf(this.eventMessage))
                .replace("%eventcountdown%", String.valueOf(this.eventTimerDisplay));

            Component compLine = mm.deserialize(parsed);
            String legacy = LegacyComponentSerializer.legacySection().serialize(compLine);

            obj.getScore(legacy).setScore(score--);
        }
    }

    public void reload() {
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
        for (Player p : Bukkit.getOnlinePlayers()) {
            showScoreboard(p);
        }
    }

    public Scoreboard getScoreboard(Player player) {
        return playerBoards.get(player);
    }

    public void showAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            showScoreboard(player);
        }
        plugin.getLogger().info("Displayed scoreboard for all players.");
    }

    public void hideAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            playerBoards.remove(player);
        }
        plugin.getLogger().info("Hid scoreboard for all players.");
    }

    public void setEventMessage(String message) {
        this.eventMessage = message;
        updateAllScoreboards();
    }

    public void setEventTimer(int minutes) {
        this.eventTimerDisplay = minutes + "m remaining";
        updateAllScoreboards();
    }

    public void clearEventTimer() {
        this.eventTimerDisplay = "";
        updateAllScoreboards();
    }

    private void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            showScoreboard(player);
        }
    }
}
