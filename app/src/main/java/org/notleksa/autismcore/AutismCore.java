package org.notleksa.autismcore;

// TODO: chat revs, maybe other shit

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.notleksa.autismcore.commands.CoreCommand;
import org.notleksa.autismcore.commands.EventCommands;
import org.notleksa.autismcore.commands.HideCommand;
import org.notleksa.autismcore.commands.InvseeCommand;
import org.notleksa.autismcore.commands.ListCommand;
import org.notleksa.autismcore.commands.MessageCommands;
import org.notleksa.autismcore.commands.MuteChatCommand;
import org.notleksa.autismcore.commands.RevTokenCommands;
import org.notleksa.autismcore.commands.ReviveCommand;
import org.notleksa.autismcore.commands.ScoreboardCommand;
import org.notleksa.autismcore.commands.SetCooldownCommand;
import org.notleksa.autismcore.commands.SetSpawnCommand;
import org.notleksa.autismcore.commands.SpawnCommand;
import org.notleksa.autismcore.commands.TeleportCommands;
import org.notleksa.autismcore.commands.TimerCommand;
import org.notleksa.autismcore.handlers.CooldownHandler;
import org.notleksa.autismcore.handlers.MuteChatHandler;
import org.notleksa.autismcore.handlers.ReviveHandler;
import org.notleksa.autismcore.handlers.ScoreboardHandler;
import org.notleksa.autismcore.handlers.ServerDataHandler;
import org.notleksa.autismcore.rat.Rat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public final class AutismCore extends JavaPlugin implements Listener {

    // core info shit
    public static final String CORE_ICON = "☘";
    public static final String VERSION = "0.7.1";
    public static final String DISCORD_LINK = "https://discord.gg/GrSeG3jR";

    // command variables
    public static boolean chatMuted = false;
    private ScoreboardHandler scoreboardHandler;
    private ReviveHandler reviveHandler;
    private boolean scoreboardEnabled = true; 
    private ServerDataHandler dataHandler;
    private CooldownHandler cooldownHandler;
    private Rat rat;


    // scoreboard because APPARENTLY ScoreboardHandler can not fucking handle the scoreboard who the fuck wrote this shit
    private File scoreboardFile;
    private FileConfiguration scoreboardConfig;

    // /core authors thingy
    public static final Map<String, TextColor> AUTHORS = new LinkedHashMap<>() {{
        put("NotLeksa", NamedTextColor.LIGHT_PURPLE);
    }};

    @Override
    public void onEnable() {
        getLogger().info("AutismCore enabled!");

        scoreboardFile = new File(getDataFolder(), "scoreboard.yml");
        if (!scoreboardFile.exists()) {
            saveResource("scoreboard.yml", false);
        }
        scoreboardConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(scoreboardFile);

        dataHandler = new ServerDataHandler(this);
        reviveHandler = new ReviveHandler(dataHandler);
        scoreboardHandler = new ScoreboardHandler(this, reviveHandler);
        cooldownHandler = new CooldownHandler(dataHandler);

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(reviveHandler, this);
        getServer().getPluginManager().registerEvents(scoreboardHandler, this);
        getServer().getPluginManager().registerEvents(new MuteChatHandler(), this);

        // Show scoreboard for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboardHandler.showScoreboard(player);
        }

        handleCommands();

        // rat
        try {
            File imageFile = new File(getDataFolder(), "rat.jpg");
    
            String imageUrl = "https://raw.githubusercontent.com/Not-Leksa/Autism-Core/master/rat.jpg";

            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            // Download the image if not present
            if (!imageFile.exists()) {
                getLogger().info("Downloading rat.jpg from GitHub...");
                try (BufferedInputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(imageFile)) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                }
                getLogger().info("rat downloaded successfully!");
            }

            String asciiArt = rat.convertToAscii(imageFile, 80);
            getLogger().info("\n" + asciiArt);

        } catch (Exception e) {
            getLogger().severe("Failed to load or render ASCII logo: " + e.getMessage());
            e.printStackTrace();
        }
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
        if (dataHandler != null) {
            dataHandler.saveAll();
        }
    }
    

    public ReviveHandler getReviveHandler() {
        return reviveHandler;
    }

    
    public ServerDataHandler getServerDataHandler() {
        return dataHandler;
    }

    public ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public CooldownHandler getCooldownHandler() {
        return cooldownHandler;
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
    public void onPlayerJoin(PlayerJoinEvent event) {

        // player join message
        String playerName = event.getPlayer().getName();
        int playerCount = event.getPlayer().getServer().getOnlinePlayers().size();

        Component joinMessage = Component.text()
                .append(Component.text(playerName, NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" joined the server (", NamedTextColor.GRAY))
                .append(Component.text(playerCount, NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(")", NamedTextColor.GRAY))
                .build();

        event.joinMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        String playerName = event.getPlayer().getName();
        int playerCount = event.getPlayer().getServer().getOnlinePlayers().size();

        Component quitMessage = Component.text()
                .append(Component.text(playerName, NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" left the server (", NamedTextColor.GRAY))
                .append(Component.text(playerCount, NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(")", NamedTextColor.GRAY))
                .build();

        event.quitMessage(quitMessage);
    }
}