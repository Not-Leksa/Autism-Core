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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ReviveHandler implements Listener {

    private final Map<UUID, Boolean> aliveStatus = new HashMap<>();
    private final Map<UUID, Integer> reviveTokens = new HashMap<>();
    private final Set<UUID> pendingReviveRequests = new HashSet<>();

    // alive dead handling

    public int getAliveCount() {
        int alive = 0;
        for (boolean status : aliveStatus.values()) {
            if (status) alive++;
        }
        return alive;
    }

    public int getDeadCount() {
        int dead = 0;
        for (boolean status : aliveStatus.values()) {
            if (!status) dead++;
        }
        return dead;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        aliveStatus.putIfAbsent(player.getUniqueId(), false); // default dead
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        aliveStatus.put(player.getUniqueId(), false);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        aliveStatus.put(player.getUniqueId(), false);
    }

    public boolean isAlive(Player player) {
        return aliveStatus.getOrDefault(player.getUniqueId(), false); // default dead
    }

    public void setAlive(Player player) {
        aliveStatus.put(player.getUniqueId(), true);
    }

    public void markDead(Player player) {
        aliveStatus.put(player.getUniqueId(), false);
    }

    // rev tokens

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
