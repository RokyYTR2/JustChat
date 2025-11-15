package dev.meyba.justChat.commands;

import dev.meyba.justChat.JustChat;
import dev.meyba.justChat.managers.ChatManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatCommands implements CommandExecutor, TabCompleter {
    private final ChatManager chatManager;
    private final JustChat plugin;

    public ChatCommands(ChatManager chatManager, JustChat plugin) {
        this.chatManager = chatManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("prefix"));

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (!sender.hasPermission("justchat.reload")) {
                        String noPermissionMsg = this.plugin.getConfig().getString("messages.no-permission");
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', noPermissionMsg));
                        return true;
                    }
                    this.plugin.loadConfig();
                    this.chatManager.reloadConfig();
                    String successMsg = this.plugin.getConfig().getString("messages.reload-success");
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', successMsg));
                    return true;
                case "help":
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "ʜᴇʟᴘ ᴍᴇɴᴜ:"));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat reload - ʀᴇʟᴏᴀᴅꜱ ᴛʜᴇ ᴄᴏɴꜰɪɢ."));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat help - ꜱʜᴏᴡꜱ ᴛʜɪꜱ ᴍᴇɴᴜ."));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat mutechat - ᴛᴏɢɢʟᴇꜱ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ ᴍᴜᴛᴇ."));
                    return true;
                case "mutechat":
                    if (!sender.hasPermission("justchat.mutechat")) {
                        String noPermissionMsg = this.plugin.getConfig().getString("messages.no-permission");
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', noPermissionMsg));
                        return true;
                    }
                    boolean newState = !chatManager.isChatMuted();
                    chatManager.setChatMuted(newState);
                    String message = newState
                            ? this.plugin.getConfig().getString("messages.chat-mute-enabled")
                            : this.plugin.getConfig().getString("messages.chat-mute-disabled");
                    plugin.getServer().broadcastMessage(prefix + ChatColor.translateAlternateColorCodes('&', message));
                    return true;
                default:
                    break;
            }
        }

        if (sender.hasPermission("justchat.help")) {
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "ʜᴇʟᴘ ᴍᴇɴᴜ:"));
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat reload - ʀᴇʟᴏᴀᴅꜱ ᴛʜᴇ ᴄᴏɴꜰɪɢ."));
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat help - ꜱʜᴏᴡꜱ ᴛʜɪꜱ ᴍᴇɴᴜ."));
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat mutechat - ᴛᴏɢɢʟᴇꜱ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ ᴍᴜᴛᴇ."));
        } else {
            String noPermissionMsg = this.plugin.getConfig().getString("messages.no-permission");
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', noPermissionMsg));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("justchat.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("justchat.help")) {
                completions.add("help");
            }
            if (sender.hasPermission("justchat.mutechat")) {
                completions.add("mutechat");
            }
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}