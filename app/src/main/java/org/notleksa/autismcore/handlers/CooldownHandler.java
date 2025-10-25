package org.notleksa.autismcore.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class CooldownHandler {

    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();
    private final Map<String, Integer> defaultCooldowns = new HashMap<>();

    private final ServerDataHandler dataHandler;

    public CooldownHandler(ServerDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public void setDefaultCooldown(String action, int seconds) {
        defaultCooldowns.put(action.toLowerCase(), seconds);
        dataHandler.setServerData("cooldowns", "tokengamble", defaultCooldowns.getOrDefault(action.toLowerCase(), 0));
    }

    public int getDefaultCooldown(String action) {
        return dataHandler.getServerInt("cooldowns", "tokengamble");
    }

    public boolean isOnCooldown(Player player, String action) {
        action = action.toLowerCase();
        Map<UUID, Long> actionCooldowns = cooldowns.get(action);
        if (actionCooldowns == null) return false;

        Long lastUse = actionCooldowns.get(player.getUniqueId());
        if (lastUse == null) return false;

        long elapsed = System.currentTimeMillis() - lastUse;
        return elapsed < (getDefaultCooldown(action) * 1000L);
    }

    public long getTimeLeft(Player player, String action) {
        action = action.toLowerCase();
        Map<UUID, Long> actionCooldowns = cooldowns.get(action);
        if (actionCooldowns == null) return 0;

        Long lastUse = actionCooldowns.get(player.getUniqueId());
        if (lastUse == null) return 0;

        long elapsed = System.currentTimeMillis() - lastUse;
        long remaining = (getDefaultCooldown(action) * 1000L) - elapsed;
        return Math.max(0, remaining / 1000L);
    }

    public void setCooldown(Player player, String action) {
        action = action.toLowerCase();
        cooldowns.computeIfAbsent(action, k -> new HashMap<>())
                .put(player.getUniqueId(), System.currentTimeMillis());
    }
}
