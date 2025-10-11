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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.notleksa.autismcore.AutismCore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ScoreboardHandler {

    private final AutismCore plugin;
    private final ReviveHandler reviveHandler;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private File scoreboardFile;
    private FileConfiguration scoreboardConfig;
    private final Map<Player, Scoreboard> playerBoards = new HashMap<>();

    public ScoreboardHandler(AutismCore plugin, ReviveHandler reviveHandler) {
        this.plugin = plugin;
        this.reviveHandler = reviveHandler;
        createScoreboardFile();
        startUpdater();
    }

    // makes scoreboard.yml if it doesnt exist
    private void createScoreboardFile() {
        scoreboardFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        if (!scoreboardFile.exists()) {
            plugin.getDataFolder().mkdirs();

            try {
                scoreboardFile.createNewFile();
                scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);

                // default values
                scoreboardConfig.set("title", "&dAutismCore");
                scoreboardConfig.set("lines", java.util.List.of(
                        "&7--------------------",
                        "&fPlayer: &d%player%",
                        "&fOnline: &d%online%/&d%max%",
                        "&fRevives: &d%revivetokens%",
                        "&7--------------------"
                ));
                scoreboardConfig.save(scoreboardFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
    }

    public void showScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();
        String title = scoreboardConfig.getString("title", "<light_purple>AutismCore");
        Objective objective = board.registerNewObjective("autismcore", "dummy",
                mm.deserialize(title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateLines(player, board, objective);
        player.setScoreboard(board);
        playerBoards.put(player, board);
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
        }.runTaskTimer(plugin, 0L, 40L); // updates every 2 seconds
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
                    .replace("%revivetokens%", String.valueOf(reviveHandler.getReviveTokens(player)));

            Component line = mm.deserialize(parsed);
            String legacy = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(line);
            objective.getScore(legacy).setScore(score--);
        }
    }

    public void reload() {
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
        for (Player player : Bukkit.getOnlinePlayers()) {
            showScoreboard(player);
        }
    }
}
