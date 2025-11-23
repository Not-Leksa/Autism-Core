package org.notleksa.autismcore.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.notleksa.autismcore.markov.MarkovChain;

public class ChatListener implements Listener {

    private final MarkovChain markov;

    public ChatListener(MarkovChain markov) {
        this.markov = markov;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().startsWith("/")) return;
        
        String msg = event.getMessage();

        msg = msg.replaceAll("§[0-9a-fk-or]", ""); // remove Minecraft color codes
        msg = msg.trim();
        if (msg.isEmpty()) return;

        markov.train(msg);
    }
}
