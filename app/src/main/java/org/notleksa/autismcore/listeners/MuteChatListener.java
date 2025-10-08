package org.notleksa.autismcore.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.notleksa.autismcore.AutismCore;

public class MuteChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (AutismCore.chatMuted && !event.getPlayer().hasPermission("autismcore.chatbypass")) {
            event.getPlayer().sendMessage(Component.text("Chat is currently muted!", net.kyori.adventure.text.format.NamedTextColor.RED));
            event.setCancelled(true);
        }
    }
}