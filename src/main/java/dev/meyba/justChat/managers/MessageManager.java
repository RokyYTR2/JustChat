package dev.meyba.justChat.managers;

import dev.meyba.justChat.JustChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager {
    private final JustChat plugin;
    private final Map<UUID, UUID> recentMessagers = new HashMap<>();
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public MessageManager(JustChat plugin) {
        this.plugin = plugin;
        loadMessagesConfig();
    }

    public void loadMessagesConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadMessagesConfig() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void setRecentMessager(UUID user1, UUID user2) {
        recentMessagers.put(user1, user2);
        recentMessagers.put(user2, user1);
    }

    public UUID getRecentMessager(UUID user) {
        return recentMessagers.get(user);
    }

    public boolean sendPrivateMessage(Player sender, Player receiver, String message) {
        if (plugin.getIgnoreManager().isIgnoring(receiver.getUniqueId(), sender.getUniqueId())) {
            return false;
        }

        String senderFormat = messagesConfig.getString("messages.private-message-format-sender");
        String receiverFormat = messagesConfig.getString("messages.private-message-format-receiver");

        String senderMessage = senderFormat
                .replace("%receiver%", receiver.getName())
                .replace("%message%", message);

        String receiverMessage = receiverFormat
                .replace("%sender%", sender.getName())
                .replace("%message%", message);

        sender.sendMessage(plugin.getChatUtils().formatMessage(senderMessage));
        receiver.sendMessage(plugin.getChatUtils().formatMessage(receiverMessage));

        setRecentMessager(sender.getUniqueId(), receiver.getUniqueId());
        return true;
    }
}