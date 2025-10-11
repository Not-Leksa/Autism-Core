package org.notleksa.autismcore.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.notleksa.autismcore.AutismCore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardHandler implements Listener {

    private final AutismCore plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private File scoreboardFile;
    private FileConfiguration scoreboardConfig;
    private final Map<Player, Scoreboard> playerBoards = new HashMap<>();

    public ScoreboardHandler(AutismCore plugin) {
        this.plugin = plugin;
        createScoreboardFile();
        startUpdater();
    }

    // creates the scoreboard.yml file with default content if it doesn't exist
    private void createScoreboardFile() {
        scoreboardFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        if (!scoreboardFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                scoreboardFile.createNewFile();
                scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);

                // the default content in question
                scoreboardConfig.set("title", "<light_purple><bold>AutismCore</bold></light_purple>");
                scoreboardConfig.set("lines", List.of(
                        "<gray>--------------------",
                        "<white>Player:</white> <light_purple>%player%",
                        "<white>Online:</white> <light_purple>%online%/%max%",
                        "<white>Revives:</white> <light_purple>%revivetokens%",
                        "<gray>--------------------"
                ));

                scoreboardConfig.save(scoreboardFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
    }

    public void showScoreboard(Player player) {
        try {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager == null) return;

            playerBoards.remove(player);

            Scoreboard board = manager.getNewScoreboard();

            Component titleComponent = mm.deserialize(scoreboardConfig.getString("title", "<light_purple>AutismCore"));

            // this shit aint even spaghetti code bro
            // this is like linguini fr
            String objectiveName = "autismcore_" + player.getUniqueId().toString().substring(0, 8);
            Objective objective = board.registerNewObjective(objectiveName, "dummy", titleComponent);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            updateLines(player, board, objective);
            player.setScoreboard(board);
            playerBoards.put(player, board);

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to show scoreboard for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }   
    }

    // PLEASE FUCKING WORK FOR THE LOVE OF PKPRO
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        showScoreboard(event.getPlayer());
    }

    private void startUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Scoreboard board = playerBoards.get(player);
                    if (board == null) {
                        showScoreboard(player);
                        continue;
                    }

                    Objective objective = board.getObjective(DisplaySlot.SIDEBAR);
                    if (objective == null) continue;

                    updateLines(player, board, objective);
                }
            }
        }.runTaskTimer(plugin, 0L, 40L); // 2 seconds (40 ticks)
    }

    private void updateLines(Player player, Scoreboard board, Objective objective) {
        board.getEntries().forEach(board::resetScores);

        List<String> lines = scoreboardConfig.getStringList("lines");
        int score = lines.size();

        for (String rawLine : lines) {
            String parsed = rawLine
                    .replace("%player%", player.getName())
                    .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()))
                    .replace("%revivetokens%", String.valueOf(plugin.getReviveHandler().getReviveTokens(player)));

            Component line = mm.deserialize(parsed);
            objective.getScore(line.toString()).setScore(score--);
        }
    }

    public void reload() {
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
        for (Player player : Bukkit.getOnlinePlayers()) {
            showScoreboard(player);
        }
    }

    public Scoreboard getScoreboard(Player player) {
        return playerBoards.get(player);
    }
}
