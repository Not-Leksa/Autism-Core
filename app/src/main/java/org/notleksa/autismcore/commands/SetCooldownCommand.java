package org.notleksa.autismcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.handlers.CooldownHandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SetCooldownCommand implements CommandExecutor {

    private final AutismCore plugin;
    private final CooldownHandler cooldownHandler;

    public SetCooldownCommand(AutismCore plugin, CooldownHandler cooldownHandler) {
        this.plugin = plugin;
        this.cooldownHandler = cooldownHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("autismcore.setcooldown")) {
            player.sendMessage(Component.text("you dont have perms idiot", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /setcooldown <type> <seconds>", NamedTextColor.YELLOW));
            return true;
        }

        String type = args[0].toLowerCase();
        try {
            int seconds = Integer.parseInt(args[1]);
            cooldownHandler.setDefaultCooldown(type, seconds);
            player.sendMessage(Component.text("Set " + type + " cooldown to " + seconds + " seconds.", NamedTextColor.GREEN));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("his ass is NOT a number", NamedTextColor.RED));
        }

        return true;
    }
}
