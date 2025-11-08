package org.notleksa.autismcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notleksa.autismcore.AutismCore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;

public class CoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Component authorsComponent = Component.empty();
        boolean first = true;
        for (var entry : AutismCore.AUTHORS.entrySet()) {
            if (!first) authorsComponent = authorsComponent.append(Component.text(", "));
            authorsComponent = authorsComponent.append(Component.text(entry.getKey(), entry.getValue()));
            first = false;
        }

        Component githubLink = Component.text("https://github.com/Not-Leksa/Autism-Core/", NamedTextColor.AQUA)
                .clickEvent(ClickEvent.openUrl("https://github.com/Not-Leksa/Autism-Core/"));

        Component message = Component.text(AutismCore.CORE_ICON + " ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text("AutismCore ", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                .append(Component.text("v" + AutismCore.VERSION, NamedTextColor.YELLOW))
                .append(Component.text("\nAuthors: ", NamedTextColor.GREEN))
                .append(authorsComponent)
                .append(Component.text("\nSpecial Thanks: pkpro, Railo_Sushi", NamedTextColor.RED))
                .append(Component.text("\n").append(githubLink));
                //.append(Component.text("\nSupport | Discord: ", NamedTextColor.BLUE))
                //.append(Component.text(AutismCore.DISCORD_LINK, NamedTextColor.BLUE).decorate(TextDecoration.UNDERLINED));

        player.sendMessage(message);

        return true;
    }
}
