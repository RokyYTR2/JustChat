package dev.meyba.justChat;

import dev.meyba.justChat.commands.Commands;
import dev.meyba.justChat.listeners.ChatListener;
import dev.meyba.justChat.managers.ChatManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class JustChat extends JavaPlugin {
    private ChatManager chatManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.chatManager = new ChatManager(this);

        this.getCommand("chat").setExecutor(new Commands(this.chatManager, this));

        this.getServer().getPluginManager().registerEvents(new ChatListener(this.chatManager, this), this);

        getLogger().info("JustChat has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustChat has been disabled!");
    }
}