package dev.meyba.justChat.listeners;

import dev.meyba.justChat.JustChat;
import dev.meyba.justChat.managers.ChatManager;
import dev.meyba.justChat.managers.IgnoreManager;
import dev.meyba.justChat.managers.MessageManager;
import dev.meyba.justChat.managers.MuteManager;
import dev.meyba.justChat.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final JustChat plugin;
    private final ChatManager chatManager;
    private final MessageManager messageManager;
    private final MuteManager muteManager;
    private final IgnoreManager ignoreManager;

    public ChatListener(JustChat plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
        this.messageManager = plugin.getMessageManager();
        this.muteManager = plugin.getMuteManager();
        this.ignoreManager = plugin.getIgnoreManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (muteManager.isMuted(player.getUniqueId())) {
            String mutedMessage = ColorUtils.translateColorCodes(messageManager.getMessagesConfig().getString("messages.player-muted"));
            player.sendMessage(mutedMessage);
            event.setCancelled(true);
            return;
        }

        String originalMessage = event.getMessage();

        if (chatManager.isChatMuted() && !player.hasPermission("justchat.mutechat.bypass")) {
            String prefix = ColorUtils.translateColorCodes(plugin.getConfig().getString("prefix"));
            String mutedMessage = ColorUtils.translateColorCodes(messageManager.getMessagesConfig().getString("messages.chat-muted"));
            player.sendMessage(prefix + mutedMessage);
            event.setCancelled(true);
            return;
        }

        String message = originalMessage;

        if (chatManager.isAntiSwearEnabled() && !player.hasPermission("justchat.antiswear.bypass")) {
            for (String blockedWord : chatManager.getBlockedWords()) {
                String regex = "(?i)" + blockedWord;
                message = message.replaceAll(regex, chatManager.getReplacement());
            }
        }

        if (player.hasPermission("justchat.color")) {
            message = ColorUtils.translateColorCodes(message);
        }

        String formattedMessage = chatManager.formatChatMessage(player, message);

        event.setCancelled(true);

        for (Player recipient : plugin.getServer().getOnlinePlayers()) {
            if (!ignoreManager.isIgnoring(recipient.getUniqueId(), player.getUniqueId())) {
                recipient.sendMessage(formattedMessage);
            }
        }

        plugin.getLogger().info("[CHAT] " + player.getName() + ": " + originalMessage);
    }
}