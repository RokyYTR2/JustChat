package dev.meyba.justChat.listeners;

import dev.meyba.justChat.JustChat;
import dev.meyba.justChat.managers.ChatManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final ChatManager chatManager;
    private final JustChat plugin;

    public ChatListener(ChatManager chatManager, JustChat plugin) {
        this.chatManager = chatManager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String originalMessage = event.getMessage();

        if (chatManager.isChatMuted() && !player.hasPermission("justchat.mutechat.bypass")) {
            String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix"));
            String mutedMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.chat-muted"));
            player.sendMessage(prefix + mutedMessage);
            event.setCancelled(true);
            return;
        }

        String message = originalMessage;
        if (player.hasPermission("justchat.color")) {
            message = ChatColor.translateAlternateColorCodes('&', originalMessage);
        }

        String formattedMessage = chatManager.formatChatMessage(player, message);

        event.setCancelled(true);

        plugin.getServer().broadcastMessage(formattedMessage);

        plugin.getLogger().info("[CHAT] " + player.getName() + ": " + originalMessage);
    }
}