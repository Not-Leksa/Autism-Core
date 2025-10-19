package org.notleksa.autismcore;

// TODO: make chat not ugly, chat revs, /tpalive /tpdead, maybe other shit

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.notleksa.autismcore.commands.*;
import org.notleksa.autismcore.handlers.*;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public final class AutismCore extends JavaPlugin implements Listener {

    // core info shit
    public static final String CORE_ICON = "☘";
    public static final String VERSION = "0.7.0";
    public static final String DISCORD_LINK = "https://discord.gg/GrSeG3jR";

    // command variables
    public static boolean chatMuted = false;
    private ScoreboardHandler scoreboardHandler;
    private ReviveHandler reviveHandler;
    private boolean scoreboardEnabled = true; 

    // scoreboard because APPARENTLY ScoreboardHandler can not fucking handle the scoreboard who the fuck wrote this shit
    private File scoreboardFile;
    private FileConfiguration scoreboardConfig;

    // /core authors thingy
    public static final Map<String, TextColor> AUTHORS = new LinkedHashMap<>() {{
        put("NotLeksa", NamedTextColor.LIGHT_PURPLE);
        put("Railo_Sushi", NamedTextColor.AQUA);
    }};

    @Override
    public void onEnable() {
        getLogger().info("AutismCore enabled!");

        // Instantiate handlers once
        reviveHandler = new ReviveHandler();
        scoreboardHandler = new ScoreboardHandler(this, reviveHandler);

        // Register event listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(reviveHandler, this);
        getServer().getPluginManager().registerEvents(scoreboardHandler, this);
        getServer().getPluginManager().registerEvents(new MuteChatHandler(), this);

        // Show scoreboard for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboardHandler.showScoreboard(player);
        }

        // Register commands
        handleCommands();
    }

    private void handleCommands() {
        // Core commands
        this.getCommand("core").setExecutor(new CoreCommand());
        this.getCommand("hide").setExecutor(new HideCommand(this));
        this.getCommand("mutechat").setExecutor(new MuteChatCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        this.getCommand("invsee").setExecutor(new InvseeCommand(this));
        this.getCommand("timer").setExecutor(new TimerCommand(this));
        this.getCommand("scoreboard").setExecutor(new ScoreboardCommand(this, scoreboardHandler));
        this.getCommand("event").setExecutor(new EventCommands(this, scoreboardHandler));

        // Revive commands
        CooldownHandler cooldownHandler = new CooldownHandler();
        this.getCommand("revive").setExecutor(new ReviveCommand(this));
        this.getCommand("list").setExecutor(new ListCommand(this, reviveHandler));

        RevTokenCommands reviveCommands = new RevTokenCommands(this, reviveHandler, cooldownHandler);
        this.getCommand("userevive").setExecutor(reviveCommands);
        this.getCommand("reviveaccept").setExecutor(reviveCommands);
        this.getCommand("addrevive").setExecutor(reviveCommands);
        this.getCommand("tokens").setExecutor(reviveCommands);
        this.getCommand("gamble").setExecutor(reviveCommands);

        getCommand("tpalive").setExecutor(new TeleportCommands(this));
        getCommand("tpdead").setExecutor(new TeleportCommands(this));



        this.getCommand("setcooldown").setExecutor(new SetCooldownCommand(this, cooldownHandler));
        

        // Message commands
        MessageCommands messageCommands = new MessageCommands();
        this.getCommand("msg").setExecutor(messageCommands);
        this.getCommand("r").setExecutor(messageCommands);
        this.getCommand("msgtoggle").setExecutor(messageCommands);
        this.getCommand("msgblock").setExecutor(messageCommands);
    }

    @Override
    public void onDisable() {
        getLogger().info("AutismCore disabled!");
    }

    public ReviveHandler getReviveHandler() {
        return reviveHandler;
    }

    public ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }


    public boolean isScoreboardEnabled() {
        return scoreboardEnabled;
    }

    public void toggleScoreboard() {
        scoreboardEnabled = !scoreboardEnabled;

        if (scoreboardEnabled) {
            getLogger().info("Scoreboard enabled — showing for all players.");
            scoreboardHandler.showAll();
        } else {
            getLogger().info("Scoreboard disabled — hiding from all players.");
            scoreboardHandler.hideAll();
        }

    
    }

    // update %alive% and %dead% for scoreboard
    // WHO THE FUCK WROTE THIS SHIT OH MY GOD 
    // leksa what the hell are you on

    public String parsePlaceholders(Player player, String message) {

        message = message
            .replace("%alive%", String.valueOf(reviveHandler.getAliveCount()))
            .replace("%dead%", String.valueOf(reviveHandler.getDeadCount()));

        return message;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String message = getConfig().getString(
            "welcome-message", // config path
            "Welcome %player%! Alive players: %online%" // default if missing
        );;
    }

    public void updateScoreboard() {
        int onlineCount = Bukkit.getOnlinePlayers().size();
        int aliveCount = reviveHandler.getAliveCount();
        int deadCount = onlineCount - aliveCount;

        List<String> lines = scoreboardConfig.getStringList("lines");

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            line = line.replace("%online%", String.valueOf(onlineCount))
                .replace("%alive%", String.valueOf(aliveCount))
                .replace("%dead%", String.valueOf(deadCount));
            lines.set(i, line);
        }

        scoreboardConfig.set("lines", lines);
        try {
            scoreboardConfig.save(scoreboardFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(Player player) {
        updateScoreboard();
    }

    @EventHandler
    public void onQuit(Player player) {
        updateScoreboard();
    }
}