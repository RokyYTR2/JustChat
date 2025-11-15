package dev.meyba.justChat;

import dev.meyba.justChat.commands.ChatCommands;
import dev.meyba.justChat.listeners.ChatListener;
import dev.meyba.justChat.managers.ChatManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class JustChat extends JavaPlugin {
    private boolean antiSwearEnabled;
    private List<String> blockedWords;
    private String replacement;
    private String antiSwearMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        ChatManager chatManager = new ChatManager(this);

        getCommand("chat").setExecutor(new ChatCommands(chatManager, this));

        getServer().getPluginManager().registerEvents(new ChatListener(chatManager, this), this);

        getLogger().info("JustChat has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustChat has been disabled!");
    }

    public void loadConfig() {
        reloadConfig();
        antiSwearEnabled = getConfig().getBoolean("anti-swear.enabled", true);
        blockedWords = getConfig().getStringList("anti-swear.blocked-words");
        replacement = getConfig().getString("anti-swear.replacement", "***");
        antiSwearMessage = getConfig().getString("anti-swear.message", "&cPlease do not use inappropriate language.");
    }

    public boolean isAntiSwearEnabled() {
        return antiSwearEnabled;
    }

    public List<String> getBlockedWords() {
        return blockedWords;
    }

    public String getReplacement() {
        return replacement;
    }
}