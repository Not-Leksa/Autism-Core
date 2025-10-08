package org.notleksa.autismcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.*;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

public class ListCommand implements CommandExecutor {

    private final AutismCore plugin;
    private final ReviveHandler reviveHandler;

    public ListCommand(AutismCore plugin, ReviveHandler reviveHandler) {
        this.plugin = plugin;
        this.reviveHandler = reviveHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1) {
            sender.sendMessage(Component.text("Usage: /list <alive|dead>", NamedTextColor.RED));
            return true;
        }

        String type = args[0].toLowerCase();
        List<String> playersList = new ArrayList<>();

        switch (type) {
            case "alive" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (reviveHandler.isAlive(player)) playersList.add(player.getName());
                }

                if (playersList.isEmpty()) {
                    sender.sendMessage(Component.text("No players are alive.", NamedTextColor.YELLOW));
                } else {
                    sender.sendMessage(Component.text("Alive players: " + String.join(", ", playersList), NamedTextColor.GREEN));
                }
            }

            case "dead" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!reviveHandler.isAlive(player)) playersList.add(player.getName());
                }

                if (playersList.isEmpty()) {
                    sender.sendMessage(Component.text("No players are dead.", NamedTextColor.YELLOW));
                } else {
                    sender.sendMessage(Component.text("Dead players: " + String.join(", ", playersList), NamedTextColor.RED));
                }
            }

            default -> sender.sendMessage(Component.text("Invalid option idiot use /list <alive|dead>", NamedTextColor.RED));
        }

        return true;
    }
}
