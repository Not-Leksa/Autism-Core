package org.notleksa.autismcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;
import org.notleksa.autismcore.gui.InvseeGUI;

public class InvseeCommand implements CommandExecutor {

    private final AutismCore plugin;

    public InvseeCommand(AutismCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player viewer)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!viewer.hasPermission("autismcore.invsee")) {
            viewer.sendMessage("You don't have permission to use this command faggot");
            return true;
        }

        if (args.length != 1) {
            viewer.sendMessage("Usage: /invsee <player>");
            return true;
        }

        // Target lookup
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            viewer.sendMessage("his ass is NOT a player");
            return true;
        }

        // Open inventory GUI
        new InvseeGUI(plugin, viewer, target).open();
        return true;
    }
}
