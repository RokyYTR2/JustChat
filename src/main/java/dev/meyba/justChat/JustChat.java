package dev.meyba.justChat;

import dev.meyba.justChat.commands.*;
import dev.meyba.justChat.listeners.ChatBubblesListener;
import dev.meyba.justChat.listeners.ChatListener;
import dev.meyba.justChat.managers.ChatManager;
import dev.meyba.justChat.managers.IgnoreManager;
import dev.meyba.justChat.managers.MessageManager;
import dev.meyba.justChat.managers.MuteManager;
import dev.meyba.justChat.utils.ChatUtils;
import dev.meyba.justChat.utils.VersionChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class JustChat extends JavaPlugin {
    private ChatManager chatManager;
    private MessageManager messageManager;
    private MuteManager muteManager;
    private IgnoreManager ignoreManager;
    private ChatUtils chatUtils;
    private ChatBubblesListener chatBubblesListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chatUtils = new ChatUtils(this);

        chatManager = new ChatManager(this);
        messageManager = new MessageManager(this);
        muteManager = new MuteManager(this);
        ignoreManager = new IgnoreManager(this);

        getCommand("chat").setExecutor(new JustChatCommand(this));
        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("ignore").setExecutor(new IgnoreCommand(this));
        getCommand("unignore").setExecutor(new UnignoreCommand(this));
        getCommand("clearchat").setExecutor(new ClearChatCommand(this));

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        chatBubblesListener = new ChatBubblesListener(this);
        getServer().getPluginManager().registerEvents(chatBubblesListener, this);

        new VersionChecker(this, "RokyYTR2", "JustChat").checkForUpdates();

        getLogger().info("JustChat has been enabled!");
    }

    @Override
    public void onDisable() {
        if (chatBubblesListener != null) {
            chatBubblesListener.cleanup();
        }
        muteManager.saveMutes();
        ignoreManager.saveIgnores();
        getLogger().info("JustChat has been disabled!");
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public IgnoreManager getIgnoreManager() {
        return ignoreManager;
    }

    public ChatUtils getChatUtils() {
        return chatUtils;
    }
}