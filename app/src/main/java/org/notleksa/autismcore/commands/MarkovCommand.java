package org.notleksa.autismcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.notleksa.autismcore.markov.MarkovChain;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MarkovCommand implements CommandExecutor {

    private final MarkovChain markov;

    public MarkovCommand(MarkovChain markov) {
        this.markov = markov;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (markov == null) {
            sender.sendMessage(Component.text("Markov is not loaded.", NamedTextColor.RED));
            return true;
        }

        int count = 10; // default

        if (args.length > 0) {
            try {
                count = Integer.parseInt(args[0]);
                if (count < 1) count = 1;
                if (count > 100) count = 100;
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Usage: /markov <length>", NamedTextColor.RED));
                return true;
            }
        }

        String generated = markov.generate(count);

        // Broadcast to everyone
        Component message = Component.text()
                .append(Component.text("[Markov] ", NamedTextColor.DARK_AQUA))
                .append(Component.text(generated, NamedTextColor.GREEN))
                .build();

        Bukkit.getServer().sendMessage(message);

        return true;
    }
}
