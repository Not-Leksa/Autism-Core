package org.notleksa.autismcore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.ScoreboardHandler;

public class ScoreboardCommand implements CommandExecutor {

    private final AutismCore plugin;
    private final ScoreboardHandler scoreboardHandler;
    private boolean toggledOffForAll = false;

    public ScoreboardCommand(AutismCore plugin, ScoreboardHandler scoreboardHandler) {
        this.plugin = plugin;
        this.scoreboardHandler = scoreboardHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /scoreboard <reload|toggle>", NamedTextColor.YELLOW));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            case "reload" -> {
                if (!player.hasPermission("autismcore.scoreboard.reload")) {
                    player.sendMessage(Component.text("you dont have perms faggot", NamedTextColor.RED));
                    return true;
                }
                scoreboardHandler.reload();
                player.sendMessage(Component.text("Scoreboard reloaded!", NamedTextColor.GREEN));
            }

            case "toggle" -> {
                if (!player.hasPermission("autismcore.scoreboard.toggle")) {
                    player.sendMessage(Component.text("you dont have perms faggot", NamedTextColor.RED));
                    return true;
                }

                toggledOffForAll = !toggledOffForAll;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (toggledOffForAll) {
                        // hide scoreboard
                        p.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
                    } else {
                        // show scoreboard
                        scoreboardHandler.showScoreboard(p);
                    }
                }

                player.sendMessage(Component.text(
                        "Scoreboard " + (toggledOffForAll ? "disabled for all players." : "enabled for all players."),
                        toggledOffForAll ? NamedTextColor.YELLOW : NamedTextColor.GREEN
                ));
            }

            default -> player.sendMessage(Component.text("his ass is NOT a command. Use /scoreboard <reload|toggle>", NamedTextColor.RED));
        }

        return true;
    }
}
