package dev.meyba.justChat.commands;

import dev.meyba.justChat.JustChat;
import dev.meyba.justChat.managers.ChatManager;
import dev.meyba.justChat.managers.MessageManager;
import dev.meyba.justChat.managers.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatCommands implements CommandExecutor, TabCompleter {
    private final JustChat plugin;
    private final ChatManager chatManager;
    private final MessageManager messageManager;
    private final MuteManager muteManager;

    public ChatCommands(JustChat plugin, ChatManager chatManager, MessageManager messageManager, MuteManager muteManager) {
        this.plugin = plugin;
        this.chatManager = chatManager;
        this.messageManager = messageManager;
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "chat":
                return handleChatCommand(sender, args);
            case "msg":
                return handleMsgCommand(sender, args);
            case "reply":
                return handleReplyCommand(sender, label, args);
            case "mute":
                return handleMuteCommand(sender, args);
            case "unmute":
                return handleUnmuteCommand(sender, args);
        }
        return false;
    }

    private boolean handleChatCommand(CommandSender sender, String[] args) {
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
                    this.muteManager.loadMutes();
                    String successMsg = this.plugin.getConfig().getString("messages.reload-success");
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', successMsg));
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
                case "help":
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "ʜᴇʟᴘ ᴍᴇɴᴜ:"));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat reload - ʀᴇʟᴏᴀᴅꜱ ᴛʜᴇ ᴄᴏɴꜰɪɢ."));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat help - ꜱʜᴏᴡꜱ ᴛʜɪꜱ ᴍᴇɴᴜ."));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat mutechat - ᴛᴏɢɢʟᴇꜱ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ ᴍᴜᴛᴇ."));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat mute <player> [time] [reason] - ᴛᴏɢɢʟᴇꜱ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ ᴍᴜᴛᴇ."));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat unmute <player> - ᴛᴏɢɢʟᴇꜱ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ ᴍᴜᴛᴇ."));
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat msg <player> [message] - ᴛᴏɢɢʟᴇꜱ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ ᴍᴜᴛᴇ."));
                    return true;
            }
        }
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "ʜᴇʟᴘ ᴍᴇɴᴜ:"));
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat reload - ʀᴇʟᴏᴀᴅꜱ ᴛʜᴇ ᴄᴏɴꜰɪɢ."));
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat help - ꜱʜᴏᴡꜱ ᴛʜɪꜱ ᴍᴇɴᴜ."));
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "/chat mutechat - ᴛᴏɢɢʟᴇꜱ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ ᴍᴜᴛᴇ."));
        return true;
    }

    private boolean handleMsgCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can send private messages.");
            return true;
        }

        if (!sender.hasPermission("justchat.msg")) {
            String noPerms = plugin.getConfig().getString("messages.no-permission");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPerms));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null) {
            String notFound = plugin.getConfig().getString("messages.player-not-found")
                    .replace("{player}", args[0]);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notFound));
            return true;
        }

        Player playerSender = (Player) sender;
        if (playerSender.getUniqueId().equals(receiver.getUniqueId())) {
            String cannotMsgSelf = plugin.getConfig().getString("messages.cannot-message-self");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', cannotMsgSelf));
            return true;
        }

        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        messageManager.sendPrivateMessage(playerSender, receiver, message);
        return true;
    }

    private boolean handleReplyCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can reply to messages.");
            return true;
        }

        if (!sender.hasPermission("justchat.reply")) {
            String noPerms = plugin.getConfig().getString("messages.no-permission");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPerms));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <message>");
            return true;
        }

        Player playerSender = (Player) sender;
        UUID receiverUUID = messageManager.getRecentMessager(playerSender.getUniqueId());

        if (receiverUUID == null) {
            String noReply = plugin.getConfig().getString("messages.no-one-to-reply-to");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noReply));
            return true;
        }

        Player receiver = Bukkit.getPlayer(receiverUUID);
        if (receiver == null) {
            String notFound = plugin.getConfig().getString("messages.player-not-found")
                    .replace("{player}", "your last contact");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notFound));
            return true;
        }

        String message = String.join(" ", args);
        messageManager.sendPrivateMessage(playerSender, receiver, message);
        return true;
    }

    private boolean handleMuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("justchat.mute")) {
            String noPerms = plugin.getConfig().getString("messages.no-permission");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPerms));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /mute <player> [time] [reason]");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            String notFound = plugin.getConfig().getString("messages.player-not-found")
                    .replace("{player}", args[0]);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notFound));
            return true;
        }

        if (muteManager.isMuted(target.getUniqueId())) {
            String alreadyMuted = plugin.getConfig().getString("messages.player-already-muted")
                    .replace("{player}", target.getName());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', alreadyMuted));
            return true;
        }

        long duration = -1;
        if (args.length > 1) {
            duration = muteManager.parseDuration(args[1]);
        }

        muteManager.mutePlayer(target.getUniqueId(), duration);

        String successMsg = plugin.getConfig().getString("messages.player-mute-success")
                .replace("{player}", target.getName());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', successMsg));

        if (target.isOnline()) {
            target.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-muted")));
        }
        return true;
    }

    private boolean handleUnmuteCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("justchat.unmute")) {
            String noPerms = plugin.getConfig().getString("messages.no-permission");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPerms));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unmute <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            String notFound = plugin.getConfig().getString("messages.player-not-found")
                    .replace("{player}", args[0]);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notFound));
            return true;
        }

        if (!muteManager.isMuted(target.getUniqueId())) {
            String notMuted = plugin.getConfig().getString("messages.player-not-muted")
                    .replace("{player}", target.getName());
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notMuted));
            return true;
        }

        muteManager.unmutePlayer(target.getUniqueId());

        String successMsg = plugin.getConfig().getString("messages.player-unmute-success")
                .replace("{player}", target.getName());
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', successMsg));
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        switch (command.getName().toLowerCase()) {
            case "chat":
                if (args.length == 1) {
                    if (sender.hasPermission("justchat.reload")) suggestions.add("reload");
                    if (sender.hasPermission("justchat.help")) suggestions.add("help");
                    if (sender.hasPermission("justchat.mutechat")) suggestions.add("mutechat");
                }
                break;
            case "msg":
            case "mute":
            case "unmute":
                if (args.length == 1) {
                    suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList()));
                }
                break;
            case "reply":
                break;
        }

        for (String s : suggestions) {
            if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                completions.add(s);
            }
        }
        return completions;
    }
}