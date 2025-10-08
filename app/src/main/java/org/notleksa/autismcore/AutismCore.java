package org.notleksa.autismcore;

import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import org.notleksa.autismcore.commands.*;
import org.notleksa.autismcore.listeners.*;

public final class AutismCore extends JavaPlugin implements Listener {

    // Core info shit
    public static final String CORE_ICON = "☘";
    public static final String VERSION = "0.0.2";
    public static final String DISCORD_LINK = "https://discord.gg/GrSeG3jR";

    // Command Variables
    public static boolean chatMuted = false;

    // /Core authors thingy
    public static final Map<String, TextColor> AUTHORS = new LinkedHashMap<>() {{
        put("NotLeksa", NamedTextColor.LIGHT_PURPLE);
        put("Railo_Sushi", NamedTextColor.AQUA);
    }};

    @Override
    public void onEnable() {
        getLogger().info("AutismCore has been enabled type shi");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new MuteChatListener(), this);

        registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("AutismCore has been disabled what the fuck i hate you");
    }

    private void registerCommands() {
        this.getCommand("core").setExecutor(new CoreCommand());
        this.getCommand("hide").setExecutor(new HideCommand(this));
        this.getCommand("mutechat").setExecutor(new MuteChatCommand(this));
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
    }
}