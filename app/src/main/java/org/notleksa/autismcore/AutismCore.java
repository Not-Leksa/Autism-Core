package org.notleksa.autismcore;

// TODO: make chat not ugly, (socialspy???), fix %dead% and %alive%, idk what else

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.notleksa.autismcore.commands.CoreCommand;
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
import org.notleksa.autismcore.commands.TimerCommand;
import org.notleksa.autismcore.handlers.CooldownHandler;
import org.notleksa.autismcore.handlers.MuteChatHandler;
import org.notleksa.autismcore.handlers.ReviveHandler;
import org.notleksa.autismcore.handlers.ScoreboardHandler;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public final class AutismCore extends JavaPlugin implements Listener {

    // core info shit
    public static final String CORE_ICON = "☘";
    public static final String VERSION = "0.6.73";
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
        getLogger().info("AutismCore has been enabled type shi");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new MuteChatHandler(), this);

        handleCommands();

        
        ReviveHandler reviveHandler2 = new ReviveHandler();
        this.reviveHandler = reviveHandler2;
        scoreboardHandler = new ScoreboardHandler(this);
        getServer().getPluginManager().registerEvents(scoreboardHandler, this);

        // show scoreboard type shi
        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboardHandler.showScoreboard(player);
        }

        scoreboardFile = new File(getDataFolder(), "scoreboard.yml");
        scoreboardConfig = YamlConfiguration.loadConfiguration(scoreboardFile);
    }

    @Override
    public void onDisable() {
        getLogger().info("AutismCore has been disabled what the fuck i hate you");
    }

    private void handleCommands() {
        this.getCommand("core").setExecutor(new CoreCommand());
        this.getCommand("hide").setExecutor(new HideCommand(this));
        this.getCommand("mutechat").setExecutor(new MuteChatCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        this.getCommand("invsee").setExecutor(new InvseeCommand(this));
        this.getCommand("timer").setExecutor(new TimerCommand(this));

        // revive commands
        CooldownHandler cooldownHandler = new CooldownHandler();
        this.getCommand("revive").setExecutor(new ReviveCommand(this, reviveHandler));
        this.getCommand("list").setExecutor(new ListCommand(this, reviveHandler));
        RevTokenCommands reviveCommands = new RevTokenCommands(this, reviveHandler, cooldownHandler);
        getCommand("userevive").setExecutor(reviveCommands);
        getCommand("reviveaccept").setExecutor(reviveCommands);
        getCommand("addrevive").setExecutor(reviveCommands);
        getCommand("tokens").setExecutor(reviveCommands);
        getCommand("gamble").setExecutor(reviveCommands);

        this.getCommand("setcooldown").setExecutor(new SetCooldownCommand(this, cooldownHandler));
        ScoreboardHandler scoreboardHandler2 = new ScoreboardHandler(this);
        getCommand("scoreboard").setExecutor(new ScoreboardCommand(this, scoreboardHandler2));

        // message commands
        MessageCommands messageCommands = new MessageCommands();
        getCommand("msg").setExecutor(messageCommands);
        getCommand("r").setExecutor(messageCommands);
        getCommand("msgtoggle").setExecutor(messageCommands);
        getCommand("msgblock").setExecutor(messageCommands);
    }

    
    public ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public ReviveHandler getReviveHandler() {
        return reviveHandler;
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