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

public class Commands implements CommandExecutor, TabCompleter {
    private final ChatManager chatManager;
    private final JustChat plugin;

    public Commands(ChatManager chatManager, JustChat plugin) {
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
                    this.plugin.reloadConfig();
                    this.chatManager.reloadConfig();
                    String successMsg = this.plugin.getConfig().getString("messages.reload-success");
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', successMsg));
                    return true;
                case "help":
                    if (!sender.hasPermission("justchat.help")) {
                        String noPermissionMsg = this.plugin.getConfig().getString("messages.no-permission");
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', noPermissionMsg));
                        return true;
                    }
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "ʜᴇʟᴘ ᴍᴇɴᴜ:"));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat reload - ʀᴇʟᴏᴀᴅꜱ ᴛʜᴇ ᴄᴏɴꜰɪɢ."));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat help - ꜱʜᴏᴡꜱ ᴛʜɪꜱ ᴍᴇɴᴜ."));
                    return true;
                default:
                    break;
            }
        }

        if (sender.hasPermission("justchat.help")) {
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "ʜᴇʟᴘ ᴍᴇɴᴜ:"));
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat reload - ʀᴇʟᴏᴀᴅꜱ ᴛʜᴇ ᴄᴏɴꜰɪɢ."));
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat help - ꜱʜᴏᴡꜱ ᴛʜɪꜱ ᴍᴇɴᴜ."));
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
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}