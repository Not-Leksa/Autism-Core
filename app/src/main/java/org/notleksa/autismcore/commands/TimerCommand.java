package org.notleksa.autismcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.notleksa.autismcore.AutismCore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TimerCommand implements CommandExecutor {

    private final AutismCore plugin;
    private BukkitRunnable activeTimer; // Track current timer

    public TimerCommand(AutismCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /timer <time> <second|minute>", NamedTextColor.RED));
            return true;
        }

        // Cancel previous timer if running
        if (activeTimer != null) {
            activeTimer.cancel();
            activeTimer = null;
        }

        int duration;
        try {
            duration = Integer.parseInt(args[0]);
            if (duration <= 0) {
                sender.sendMessage(Component.text("enter a number bigger than 0 dumbass", NamedTextColor.RED));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("thats not a fucking number???", NamedTextColor.RED));
            return true;
        }

        String unit = args[1].toLowerCase();
        int totalSeconds = switch (unit) {
            case "second", "seconds" -> duration;
            case "minute", "minutes" -> duration * 60;
            default -> {
                sender.sendMessage(Component.text("Invalid unit, use 'second' or 'minute'.", NamedTextColor.RED));
                yield -1;
            }
        };

        if (totalSeconds == -1) return true;

        sender.sendMessage(Component.text("Timer set for " + duration + " " + unit + ".", NamedTextColor.GREEN));

        activeTimer = new BukkitRunnable() {
            int timeLeft = totalSeconds;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    Component complete = Component.text("Time's up!", NamedTextColor.GOLD);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(complete);
                    }
                    cancel();
                    activeTimer = null; // cancels previous timer
                    return;
                }

                // Update every minute and for last 10 seconds
                if (timeLeft % 60 == 0 || timeLeft <= 10) {
                    int mins = timeLeft / 60;
                    int secs = timeLeft % 60;
                    String formatted = (mins > 0 ? mins + "m " : "") + (secs > 0 ? secs + "s" : "");
                    Component update = Component.text("⏳ " + formatted + " remaining", NamedTextColor.YELLOW);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(update);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
                    }
                }

                timeLeft--;
            }
        };

        activeTimer.runTaskTimer(plugin, 0L, 20L); 

        return true;
    }
}
