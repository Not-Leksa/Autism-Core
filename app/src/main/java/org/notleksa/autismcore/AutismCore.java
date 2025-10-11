package org.notleksa.autismcore;

// TODO: msg, socialspy, scoreboard, make chat not ugly

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
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
    public static final String VERSION = "0.1.1";
    public static final String DISCORD_LINK = "https://discord.gg/GrSeG3jR";

    // command variables
    public static boolean chatMuted = false;
    private ScoreboardHandler scoreboardHandler;
    private ReviveHandler reviveHandler;

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

        
        ReviveHandler reviveHandler = new ReviveHandler();
        this.reviveHandler = reviveHandler;
        this.scoreboardHandler = new ScoreboardHandler(this);
        getServer().getPluginManager().registerEvents(scoreboardHandler, this);

        // show scoreboard type shi
        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboardHandler.showScoreboard(player);
        }
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
        this.getCommand("scoreboard").setExecutor(new ScoreboardCommand(this, scoreboardHandler));
    }

    
    public ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public ReviveHandler getReviveHandler() {
        return reviveHandler;
    }
}