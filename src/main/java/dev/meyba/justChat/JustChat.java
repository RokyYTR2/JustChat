package dev.meyba.justChat;

import dev.meyba.justChat.commands.ChatCommands;
import dev.meyba.justChat.listeners.ChatListener;
import dev.meyba.justChat.managers.ChatManager;
import dev.meyba.justChat.managers.MessageManager;
import dev.meyba.justChat.managers.MuteManager;
import dev.meyba.justChat.utils.VersionChecker;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class JustChat extends JavaPlugin {
    private ChatManager chatManager;
    private MuteManager muteManager;
    private boolean antiSwearEnabled;
    private List<String> blockedWords;
    private String replacement;
    private String antiSwearMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        chatManager = new ChatManager(this);
        MessageManager messageManager = new MessageManager(this);
        muteManager = new MuteManager(this);

        ChatCommands commandExecutor = new ChatCommands(this, chatManager, messageManager, muteManager);
        getCommand("chat").setExecutor(commandExecutor);
        getCommand("msg").setExecutor(commandExecutor);
        getCommand("reply").setExecutor(commandExecutor);
        getCommand("mute").setExecutor(commandExecutor);
        getCommand("unmute").setExecutor(commandExecutor);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        new VersionChecker(this, "RokyYTR2", "JustChat").checkForUpdates();

        getLogger().info("JustChat has been enabled!");
    }

    @Override
    public void onDisable() {
        muteManager.saveMutes();
        getLogger().info("JustChat has been disabled!");
    }

    public void loadConfig() {
        reloadConfig();
        antiSwearEnabled = getConfig().getBoolean("anti-swear.enabled");
        blockedWords = getConfig().getStringList("anti-swear.blocked-words");
        replacement = getConfig().getString("anti-swear.replacement");
        antiSwearMessage = getConfig().getString("anti-swear.message");
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public MuteManager getMuteManager() {
        return muteManager;
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