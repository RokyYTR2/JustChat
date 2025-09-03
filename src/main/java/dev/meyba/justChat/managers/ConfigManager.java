package dev.meyba.justChat.managers;

import dev.meyba.justChat.JustChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;

public class ConfigManager {
    private final JustChat plugin;
    private FileConfiguration config;

    public ConfigManager(JustChat plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public String getChatFormat(String group) {
        String format = config.contains("chat-formats." + group)
                ? config.getString("chat-formats." + group)
                : config.getString("chat-formats.default", "{player}: {message}");

        return ChatColor.translateAlternateColorCodes('&', format);
    }
}