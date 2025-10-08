package org.notleksa.autismcore.commands;

import org.notleksa.autismcore.AutismCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

public class MuteChatCommand implements CommandExecutor {

    private final AutismCore plugin;

    public MuteChatCommand(AutismCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        // Toggle chat mute
        AutismCore.chatMuted = !AutismCore.chatMuted;

        String status = AutismCore.chatMuted ? "muted" : "unmuted";
        plugin.getServer().broadcast(Component.text("Chat has been " + status + " by " + sender.getName() + "!", net.kyori.adventure.text.format.NamedTextColor.RED));
        return true;
    }
}