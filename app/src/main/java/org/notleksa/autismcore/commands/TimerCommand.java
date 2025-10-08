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

    public TimerCommand(AutismCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /timer <time> <second|minute>", NamedTextColor.RED));
            return true;
        }

        int duration;
        try {
            duration = Integer.parseInt(args[0]);
            if (duration <= 0) {
                sender.sendMessage(Component.text("Enter a number thats greater than 0 faggot", NamedTextColor.RED));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("His ass is NOT a fucking dumber", NamedTextColor.RED));
            return true;
        }

        String unit = args[1].toLowerCase();
        int totalSeconds = switch (unit) {
            case "second", "seconds" -> duration;
            case "minute", "minutes" -> duration * 60;
            default -> {
                sender.sendMessage(Component.text("thats not a good time unit, use like 'minute' or 'second' or smth", NamedTextColor.RED));
                yield -1;
            }
        };

        if (totalSeconds == -1) return true;

        sender.sendMessage(Component.text("Timer set for" + duration + " " + unit + ".", NamedTextColor.GREEN));

        new BukkitRunnable() {
            int timeLeft = totalSeconds;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    var complete = Component.text("times up !!! fr", NamedTextColor.GOLD);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(complete);
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    }
                    cancel();
                    return;
                }

                // Update every minute and for the last 10 seconds
                if (timeLeft % 60 == 0 || timeLeft <= 10) {
                    int mins = timeLeft / 60;
                    int secs = timeLeft % 60;
                    String formatted = (mins > 0 ? mins + "m " : "") + (secs > 0 ? secs + "s" : "");
                    var update = Component.text("⏳ " + formatted + " remaining", NamedTextColor.YELLOW);

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(update);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
                    }
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        return true;
    }
}
