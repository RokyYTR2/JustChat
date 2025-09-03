package dev.meyba.justChat.listeners;

import dev.meyba.justChat.JustChat;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final JustChat plugin;

    public ChatListener(JustChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String group = plugin.getLuckPermsHook().getPrimaryGroup(event.getPlayer());

        String format = plugin.getConfigManager().getChatFormat(group);

        String message = ChatColor.translateAlternateColorCodes('&', event.getMessage());

        format = format.replace("{player}", event.getPlayer().getName())
                .replace("{message}", message)
                .replace("{luckperms_prefix}", ChatColor.translateAlternateColorCodes('&', plugin.getLuckPermsHook().getPrimaryGroup(event.getPlayer())));

        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            format = PlaceholderAPI.setPlaceholders(event.getPlayer(), format);
        }

        event.setCancelled(true);
        event.getPlayer().getServer().broadcastMessage(format);
    }
}