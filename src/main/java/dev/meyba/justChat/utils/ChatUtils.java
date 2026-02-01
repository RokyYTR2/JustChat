package dev.meyba.justChat.utils;

import dev.meyba.justChat.JustChat;

public class ChatUtils {
    private final JustChat plugin;

    public ChatUtils(JustChat plugin) {
        this.plugin = plugin;
    }

    public String getPrefix() {
        String prefix = plugin.getConfig().getString("prefix");
        return ColorUtils.translateColorCodes(prefix);
    }

    public String formatMessage(String message) {
        return getPrefix() + ColorUtils.translateColorCodes(message);
    }

    public String getMessage(String path) {
        return plugin.getMessageManager().getMessagesConfig().getString("messages." + path, "&cMessage not found: " + path);
    }

    public String translateColorCodes(String text) {
        return ColorUtils.translateColorCodes(text);
    }
}
