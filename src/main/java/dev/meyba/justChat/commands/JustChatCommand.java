package dev.meyba.justChat.commands;

import dev.meyba.justChat.JustChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JustChatCommand implements CommandExecutor, TabCompleter {
    private final JustChat plugin;

    public JustChatCommand(JustChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("justchat.reload")) {
                    sender.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("no-permission")));
                    return true;
                }
                plugin.reloadConfig();
                plugin.getMessageManager().reloadMessagesConfig();
                plugin.getChatManager().reloadConfig();
                plugin.getMuteManager().loadMutes();
                sender.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("reload-success")));
                return true;

            case "mutechat":
                if (!sender.hasPermission("justchat.mutechat")) {
                    sender.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("no-permission")));
                    return true;
                }
                boolean newState = !plugin.getChatManager().isChatMuted();
                plugin.getChatManager().setChatMuted(newState);
                String message = newState
                        ? plugin.getChatUtils().getMessage("chat-mute-enabled")
                        : plugin.getChatUtils().getMessage("chat-mute-disabled");
                plugin.getServer().broadcastMessage(plugin.getChatUtils().formatMessage(message));
                return true;

            case "msg":
                return forwardCommand(sender, "msg", args);

            case "reply":
                return forwardCommand(sender, "reply", args);

            case "mute":
                return forwardCommand(sender, "mute", args);

            case "unmute":
                return forwardCommand(sender, "unmute", args);

            case "ignore":
                return forwardCommand(sender, "ignore", args);

            case "unignore":
                return forwardCommand(sender, "unignore", args);

            case "clearchat":
                return forwardCommand(sender, "clearchat", args);

            case "help":
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        String version = plugin.getDescription().getVersion();

        sender.sendMessage(plugin.getChatUtils().translateColorCodes("&#CFBC4D&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("     &#CFBC4D&lᴊ&#D1BD4D&lᴜ&#D2BD4C&lꜱ&#D4BE4C&lᴛ&#D6BE4B&lᴄ&#D8BF4B&lʜ&#D9BF4A&lᴀ&#DBC04A&lᴛ   &#FFFFFFᴄᴏᴍᴍᴀɴᴅꜱ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("&#CFBC4D&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getChatUtils().translateColorCodes(" &#CFBC4D&l● &#FFFFFFᴄᴏʀᴇ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ᴄʜᴀᴛ ʀᴇʟᴏᴀᴅ &#8E8E8E- &#7F7F7Fʀᴇʟᴏᴀᴅ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ᴄʜᴀᴛ ʜᴇʟᴘ &#8E8E8E- &#7F7F7Fꜱʜᴏᴡ ᴛʜɪꜱ ᴍᴇɴᴜ"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getChatUtils().translateColorCodes(" &#CFBC4D&l● &#FFFFFFᴄʜᴀᴛ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ᴍꜱɢ <ᴘʟᴀʏᴇʀ> <ᴍᴇꜱꜱᴀɢᴇ> &#8E8E8E- &#7F7F7Fꜱᴇɴᴅ ᴘʀɪᴠᴀᴛᴇ ᴍᴇꜱꜱᴀɢᴇ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ʀᴇᴘʟʏ <ᴍᴇꜱꜱᴀɢᴇ> &#8E8E8E- &#7F7F7Fʀᴇᴘʟʏ ᴛᴏ ʟᴀꜱᴛ ᴍᴇꜱꜱᴀɢᴇ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ɪɢɴᴏʀᴇ <ᴘʟᴀʏᴇʀ> &#8E8E8E- &#7F7F7Fɪɢɴᴏʀᴇ ᴘʟᴀʏᴇʀ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ᴜɴɪɢɴᴏʀᴇ <ᴘʟᴀʏᴇʀ> &#8E8E8E- &#7F7F7Fᴜɴɪɢɴᴏʀᴇ ᴘʟᴀʏᴇʀ"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getChatUtils().translateColorCodes(" &#CFBC4D&l● &#FFFFFFᴍᴏᴅᴇʀᴀᴛɪᴏɴ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ᴍᴜᴛᴇ <ᴘʟᴀʏᴇʀ> [ᴛɪᴍᴇ] &#8E8E8E- &#7F7F7Fᴍᴜᴛᴇ ᴘʟᴀʏᴇʀ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ᴜɴᴍᴜᴛᴇ <ᴘʟᴀʏᴇʀ> &#8E8E8E- &#7F7F7Fᴜɴᴍᴜᴛᴇ ᴘʟᴀʏᴇʀ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ᴄʜᴀᴛ ᴍᴜᴛᴇᴄʜᴀᴛ &#8E8E8E- &#7F7F7Fᴛᴏɢɢʟᴇ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#DBC04A/ᴄʟᴇᴀʀᴄʜᴀᴛ &#8E8E8E- &#7F7F7Fᴄʟᴇᴀʀ ᴄʜᴀᴛ"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getChatUtils().translateColorCodes(" &#8E8E8Eᴠᴇʀꜱɪᴏɴ: &#DBC04A" + version));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("&#CFBC4D&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage("");
    }

    private boolean forwardCommand(CommandSender sender, String commandName, String[] args) {
        if (args.length <= 1) {
            return Bukkit.dispatchCommand(sender, commandName);
        }
        String forwardArgs = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        return Bukkit.dispatchCommand(sender, commandName + " " + forwardArgs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("justchat.reload")) suggestions.add("reload");
            if (sender.hasPermission("justchat.help")) suggestions.add("help");
            if (sender.hasPermission("justchat.mutechat")) suggestions.add("mutechat");
            if (sender.hasPermission("justchat.msg")) suggestions.add("msg");
            if (sender.hasPermission("justchat.reply")) suggestions.add("reply");
            if (sender.hasPermission("justchat.mute")) suggestions.add("mute");
            if (sender.hasPermission("justchat.unmute")) suggestions.add("unmute");
            if (sender.hasPermission("justchat.ignore")) suggestions.add("ignore");
            if (sender.hasPermission("justchat.ignore")) suggestions.add("unignore");
            if (sender.hasPermission("justchat.clearchat")) suggestions.add("clearchat");
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if ((sub.equals("msg") && sender.hasPermission("justchat.msg")) ||
                (sub.equals("mute") && sender.hasPermission("justchat.mute")) ||
                (sub.equals("unmute") && sender.hasPermission("justchat.unmute")) ||
                (sub.equals("ignore") && sender.hasPermission("justchat.ignore")) ||
                (sub.equals("unignore") && sender.hasPermission("justchat.ignore"))) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }
        }

        for (String s : suggestions) {
            if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                completions.add(s);
            }
        }

        return completions;
    }
}
