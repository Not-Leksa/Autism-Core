package org.notleksa.autismcore.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.notleksa.autismcore.AutismCore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class RevivalCommands implements CommandExecutor, Listener {

    private final AutismCore plugin;
    private boolean active = false;
    private String correctAnswer = "";
    private int winnerCount = 0;
    private final List<Player> winners = new ArrayList<>();

    public RevivalCommands(AutismCore plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("autismcore.revival")) {
            player.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(Component.text("Usage: /revival <winners> \"<question>\" \"<answer>\"", NamedTextColor.RED));
            return true;
        }

        String full = String.join(" ", args);

        // what the fuck is this shit
        Pattern pattern = Pattern.compile("^(\\d+)\\s+\"([^\"]+)\"\\s+\"([^\"]+)\"$");
        Matcher matcher = pattern.matcher(full);

        if (!matcher.find()) {
            player.sendMessage(Component.text("Invalid format! Use: /revival <winners> \"<question>\" \"<answer>\"", NamedTextColor.RED));
            return true;
        }

        try {
            winnerCount = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("First argument must be a number (number of winners).", NamedTextColor.RED));
            return true;
        }

        String question = matcher.group(2);
        correctAnswer = matcher.group(3).toLowerCase();

        if (active) {
            player.sendMessage(Component.text("theres already a revival idiot", NamedTextColor.YELLOW));
            return true;
        }

        active = true;
        winners.clear();

        Bukkit.broadcast(Component.text("[Revival] ", NamedTextColor.GOLD)
                .append(Component.text("Question: ", NamedTextColor.YELLOW))
                .append(Component.text(question, NamedTextColor.AQUA)));

        Bukkit.broadcast(Component.text("First " + winnerCount + " players to answer correctly win!", NamedTextColor.GRAY));

        return true;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!active) return;

        Player player = event.getPlayer();
        String message = event.getMessage().trim().toLowerCase();

        if (message.equalsIgnoreCase(correctAnswer)) {
            if (winners.contains(player)) return;

            winners.add(player);

            if (winners.size() >= winnerCount) {
                endQuestion();
            }
        }
    }

    private void endQuestion() {
        active = false;

        Bukkit.broadcast(Component.text("[Revival] ", NamedTextColor.GOLD)
                .append(Component.text("The question has ended!", NamedTextColor.GREEN)));

        Bukkit.broadcast(Component.text("Winners:", NamedTextColor.GOLD));
        for (Player p : winners) {
            Bukkit.broadcast(Component.text(" - ", NamedTextColor.GRAY)
                    .append(Component.text(p.getName(), NamedTextColor.AQUA)));
        }
    }
}
