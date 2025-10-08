package org.notleksa.autismcore;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.notleksa.autismcore.commands.CoreCommand;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public final class AutismCore extends JavaPlugin implements Listener {

    public static final String CORE_ICON = "☘";
    public static final String VERSION = "0.0.2";
    public static final String DISCORD_LINK = "https://discord.gg/GrSeG3jR";

    // LinkedHashMap preserves order
    public static final Map<String, TextColor> AUTHORS = new LinkedHashMap<>() {{
        put("NotLeksa", NamedTextColor.LIGHT_PURPLE);
        put("Railo_Sushi", NamedTextColor.GREEN);
    }};

    @Override
    public void onEnable() {
        getLogger().info("AutismCore has been enabled type shi");
        getServer().getPluginManager().registerEvents(this, this);

        registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("AutismCore has been disabled what the fuck i hate you");
    }

    private void registerCommands() {
        this.getCommand("core").setExecutor(new CoreCommand());
    }
}