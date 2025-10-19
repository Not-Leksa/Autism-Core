package org.notleksa.autismcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.ScoreboardHandler;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class ScoreboardCommand implements CommandExecutor {

    private final AutismCore plugin;
    private final ScoreboardHandler scoreboardHandler;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ScoreboardCommand(AutismCore plugin, ScoreboardHandler scoreboardHandler) {
        this.plugin = plugin;
        this.scoreboardHandler = scoreboardHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(mm.deserialize("<red>Usage:</red> /scoreboard <reload|toggle>"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("autismcore.scoreboard.reload")) {
                    sender.sendMessage(mm.deserialize("<red>you dont have perms idiot</red>"));
                    return true;
                }

                if (scoreboardHandler == null) {
                    sender.sendMessage(mm.deserialize("<red>Scoreboard handler not initialized! Check plugin setup.</red>"));
                    plugin.getLogger().severe("Scoreboard handler is null when trying to reload!");
                    return true;
                }

                scoreboardHandler.reload();
                sender.sendMessage(mm.deserialize("<green>Scoreboard reloaded successfully.</green>"));
            }

            case "toggle" -> {
                if (!sender.hasPermission("autismcore.scoreboard.toggle")) {
                    sender.sendMessage(mm.deserialize("<red>you dont have perms idiot</red>"));
                    return true;
                }

                plugin.toggleScoreboard();
                sender.sendMessage(mm.deserialize("<yellow>Scoreboard visibility toggled for all players.</yellow>"));
            }

            default -> sender.sendMessage(mm.deserialize("<red>Usage:</red> /scoreboard <reload|toggle>"));
        }

        return true;
    }
}
