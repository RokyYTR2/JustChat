package dev.meyba.justChat;

import dev.meyba.justChat.commands.ChatCommands;
import dev.meyba.justChat.listeners.ChatListener;
import dev.meyba.justChat.managers.ChatManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class JustChat extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ChatManager chatManager = new ChatManager(this);

        getCommand("chat").setExecutor(new ChatCommands(chatManager, this));

        getServer().getPluginManager().registerEvents(new ChatListener(chatManager, this), this);

        getLogger().info("JustChat has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustChat has been disabled!");
    }
}