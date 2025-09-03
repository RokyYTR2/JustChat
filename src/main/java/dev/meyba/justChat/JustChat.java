package dev.meyba.justChat;

import dev.meyba.justChat.hooks.LuckPermsHook;
import dev.meyba.justChat.listeners.ChatListener;
import dev.meyba.justChat.managers.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class JustChat extends JavaPlugin {
    private ConfigManager configManager;
    private LuckPermsHook luckPermsHook;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        luckPermsHook = new LuckPermsHook(this);
        if (!luckPermsHook.isEnabled()) {
            getLogger().warning("LuckPerms not found! Some features may be limited.");
        }

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getLogger().info("JustChat has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustChat has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LuckPermsHook getLuckPermsHook() {
        return luckPermsHook;
    }
}