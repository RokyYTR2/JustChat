package dev.meyba.justChat.commands;

import dev.meyba.justChat.JustChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {
    private final JustChat plugin;

    public ClearChatCommand(JustChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("justchat.clearchat")) {
            sender.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("no-permission")));
            return true;
        }

        for (Player online : plugin.getServer().getOnlinePlayers()) {
            for (int i = 0; i < 500; i++) {
                online.sendMessage("");
            }
        }

        String clearedMessage = plugin.getChatUtils().getMessage("chat-cleared")
                .replace("%player%", sender.getName());
        plugin.getServer().broadcastMessage(plugin.getChatUtils().formatMessage(clearedMessage));

        return true;
    }
}