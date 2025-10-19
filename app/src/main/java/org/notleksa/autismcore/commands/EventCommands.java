package org.notleksa.autismcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.ScoreboardHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class EventCommands implements CommandExecutor {

    private final AutismCore plugin;
    private final ScoreboardHandler scoreboardHandler;
    private BukkitRunnable activeTimer = null;

    public EventCommands(AutismCore plugin, ScoreboardHandler scoreboardHandler) {
        this.plugin = plugin;
        this.scoreboardHandler = scoreboardHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /" + label + " <msg|timer> ...", NamedTextColor.RED));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "msg" -> handleEventMessage(sender, args);
            case "timer" -> handleEventTimer(sender, args);
            default -> sender.sendMessage(Component.text("Invalid subcommand. Use /eventmsg or /eventtimer.", NamedTextColor.RED));
        }

        return true;
    }

    // /eventmsg
    private void handleEventMessage(CommandSender sender, String[] args) {
        if (!sender.hasPermission("autismcore.event")) {
            sender.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /eventmsg <message>", NamedTextColor.RED));
            return;
        }

        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        // Send to scoreboard handler
        scoreboardHandler.setEventMessage(message);

        Bukkit.broadcast(Component.text("[Event] ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(message, NamedTextColor.WHITE)));

        sender.sendMessage(Component.text("Event message set to: ", NamedTextColor.GREEN)
                .append(Component.text(message, NamedTextColor.LIGHT_PURPLE)));
    }

    // /eventtimer
    private void handleEventTimer(CommandSender sender, String[] args) {
        if (!sender.hasPermission("autismcore.eventtimer")) {
            sender.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /eventtimer <minutes>", NamedTextColor.RED));
            return;
        }

        int minutes;
        try {
            minutes = Integer.parseInt(args[1]);
            if (minutes <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Use a valid number of minutes > 0.", NamedTextColor.RED));
            return;
        }

        // Cancel existing timer if running
        if (activeTimer != null) {
            activeTimer.cancel();
            activeTimer = null;
        }

        sender.sendMessage(Component.text("Started event timer for " + minutes + " minutes.", NamedTextColor.GREEN));

        scoreboardHandler.setEventTimer(minutes);

        activeTimer = new BukkitRunnable() {
            int remaining = minutes * 60; // convert to seconds

            @Override
            public void run() {
                if (remaining <= 0) {
                    Bukkit.broadcast(Component.text("⏰ Event timer has ended!", NamedTextColor.GOLD));
                    scoreboardHandler.clearEventTimer();
                    cancel();
                    return;
                }

                // Update every minute
                if (remaining % 60 == 0) {
                    int mins = remaining / 60;
                    Bukkit.broadcast(Component.text("⏳ " + mins + " minute" + (mins == 1 ? "" : "s") + " remaining!", NamedTextColor.YELLOW));
                    scoreboardHandler.setEventTimer(mins);
                }

                remaining--;
            }
        };

        activeTimer.runTaskTimer(plugin, 0L, 20L);
    }
}
