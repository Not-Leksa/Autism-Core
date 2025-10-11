package org.notleksa.autismcore.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.notleksa.autismcore.AutismCore;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

public class MuteChatHandler implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (AutismCore.chatMuted && !event.getPlayer().hasPermission("autismcore.chatbypass")) {
            event.getPlayer().sendMessage(Component.text("Chat is currently muted!", net.kyori.adventure.text.format.NamedTextColor.RED));
            event.setCancelled(true);
        }
    }
}