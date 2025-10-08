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
            sender.sendMessage("Usage: /timer <amount> <second|minute>");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid number: " + args[0]);
            return true;
        }

        String unit = args[1].toLowerCase();
        int totalSeconds;

        switch (unit) {
            case "second", "seconds" -> totalSeconds = amount;
            case "minute", "minutes" -> totalSeconds = amount * 60;
            default -> {
                sender.sendMessage("Invalid unit: use 'second' or 'minute'");
                return true;
            }
        }

        sender.sendMessage("Timer started for " + amount + " " + unit + "!");

        new BukkitRunnable() {
            int remaining = totalSeconds;

            @Override
            public void run() {
                if (remaining <= 0) {
                    Component finished = Component.text("Timer finished!", NamedTextColor.GREEN);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(finished);
                        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    }
                    cancel();
                    return;
                }

                // Notify at every minute and also last 10 seconds
                if (remaining % 60 == 0 || remaining <= 10) {
                    int minutes = remaining / 60;
                    int seconds = remaining % 60;
            
                    String timeString = (minutes > 0 ? minutes + "m " : "") + (seconds > 0 ? seconds + "s" : "");

                    Component message = Component.text("Timer: " + timeString + " remaining!", NamedTextColor.YELLOW);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(message);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
                    }
                }

                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // Runs every second

        return true;
    }
}
