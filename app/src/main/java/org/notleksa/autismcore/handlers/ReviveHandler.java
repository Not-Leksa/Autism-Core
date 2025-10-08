package org.notleksa.autismcore.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ReviveHandler implements Listener {

    private final Map<UUID, Boolean> aliveStatus = new HashMap<>();
    private final Map<UUID, Integer> reviveTokens = new HashMap<>();
    private final Set<UUID> pendingReviveRequests = new HashSet<>();

    // Alive and dead handling
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        aliveStatus.put(player.getUniqueId(), false); // mark dead
    }

    public boolean isAlive(Player player) {
        return aliveStatus.getOrDefault(player.getUniqueId(), false); // default dead
    }

    public void setAlive(Player player, boolean alive) {
        aliveStatus.put(player.getUniqueId(), alive); // mark alive
    }

    public void markDead(Player player) {
        aliveStatus.put(player.getUniqueId(), false);
    }

    // Rev Tokens
    public int getReviveTokens(Player player) {
        return reviveTokens.getOrDefault(player.getUniqueId(), 0);
    }

    public void addReviveTokens(Player player, int amount) {
        reviveTokens.put(player.getUniqueId(), getReviveTokens(player) + amount);
    }

    public boolean useReviveToken(Player player) {
        int current = getReviveTokens(player);
        if (current <= 0) return false;
        reviveTokens.put(player.getUniqueId(), current - 1);
        return true;
    }

    public void addReviveRequest(Player player) {
        pendingReviveRequests.add(player.getUniqueId());
    }

    public boolean hasPendingRevive(Player player) {
        return pendingReviveRequests.contains(player.getUniqueId());
    }

    public void removeReviveRequest(Player player) {
        pendingReviveRequests.remove(player.getUniqueId());
    }

    public Set<UUID> getPendingReviveRequests() {
        return new HashSet<>(pendingReviveRequests);
    }

    public void clearAllRequests() {
        pendingReviveRequests.clear();
    }
}
