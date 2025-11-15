package dev.meyba.justChat.managers;

import dev.meyba.justChat.JustChat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChatManager {
    private final JustChat plugin;
    private LuckPerms luckPermsAPI;
    private boolean placeholderAPIEnabled;
    private boolean isChatMuted;

    public ChatManager(JustChat plugin) {
        this.plugin = plugin;
        this.isChatMuted = false;
        initializeHooks();
    }

    private void initializeHooks() {
        try {
            this.luckPermsAPI = Bukkit.getServicesManager().load(LuckPerms.class);
            if (this.luckPermsAPI != null) {
                plugin.getLogger().info("LuckPerms hook initialized successfully!");
            } else {
                plugin.getLogger().warning("LuckPerms not found! Using default group 'default'.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to hook into LuckPerms: " + e.getMessage());
            this.luckPermsAPI = null;
        }

        this.placeholderAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (this.placeholderAPIEnabled) {
            plugin.getLogger().info("PlaceholderAPI hook initialized successfully!");
        } else {
            plugin.getLogger().warning("PlaceholderAPI not found! Placeholders will not be processed.");
        }
    }

    public void reloadConfig() {
        initializeHooks();
    }

    public boolean isChatMuted() {
        return isChatMuted;
    }

    public void setChatMuted(boolean muted) {
        this.isChatMuted = muted;
    }

    public String getPrimaryGroup(Player player) {
        if (luckPermsAPI == null) {
            return "default";
        }

        try {
            User user = luckPermsAPI.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                return user.getPrimaryGroup();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get primary group for " + player.getName() + ": " + e.getMessage());
        }

        return "default";
    }

    public String getPrefix(Player player) {
        if (luckPermsAPI == null) {
            return "";
        }

        try {
            User user = luckPermsAPI.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                String prefix = user.getCachedData().getMetaData().getPrefix();
                return prefix != null ? prefix : "";
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get prefix for " + player.getName() + ": " + e.getMessage());
        }

        return "";
    }

    public String getSuffix(Player player) {
        if (luckPermsAPI == null) {
            return "";
        }

        try {
            User user = luckPermsAPI.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                String suffix = user.getCachedData().getMetaData().getSuffix();
                return suffix != null ? suffix : "";
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get suffix for " + player.getName() + ": " + e.getMessage());
        }

        return "";
    }

    public String formatChatMessage(Player player, String message) {
        FileConfiguration config = plugin.getConfig();
        String group = getPrimaryGroup(player);

        String format = config.getString("chat-formats." + group,
                config.getString("chat-formats.default", "{prefix}{player}{suffix}: {message}"));

        format = format.replace("{player}", player.getName())
                .replace("{message}", message)
                .replace("{group}", group)
                .replace("{prefix}", getPrefix(player))
                .replace("{suffix}", getSuffix(player));

        if (placeholderAPIEnabled) {
            try {
                format = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to process PlaceholderAPI placeholders: " + e.getMessage());
            }
        }

        return ChatColor.translateAlternateColorCodes('&', format);
    }
}