package dev.meyba.justChat.managers;

import dev.meyba.justChat.JustChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager {
    private final JustChat plugin;
    private final Map<UUID, UUID> recentMessagers = new HashMap<>();

    public MessageManager(JustChat plugin) {
        this.plugin = plugin;
    }

    public void setRecentMessager(UUID user1, UUID user2) {
        recentMessagers.put(user1, user2);
        recentMessagers.put(user2, user1);
    }

    public UUID getRecentMessager(UUID user) {
        return recentMessagers.get(user);
    }

    public void sendPrivateMessage(Player sender, Player receiver, String message) {
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix"));

        String senderFormat = plugin.getConfig().getString("messages.private-message-format-sender");
        String receiverFormat = plugin.getConfig().getString("messages.private-message-format-receiver");

        String senderMessage = senderFormat
                .replace("{receiver}", receiver.getName())
                .replace("{message}", message);

        String receiverMessage = receiverFormat
                .replace("{sender}", sender.getName())
                .replace("{message}", message);

        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', senderMessage));
        receiver.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', receiverMessage));

        setRecentMessager(sender.getUniqueId(), receiver.getUniqueId());
    }
}