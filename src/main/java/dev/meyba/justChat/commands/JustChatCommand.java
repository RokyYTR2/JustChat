package dev.meyba.justChat.commands;

import dev.meyba.justChat.JustChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
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

            case "help":
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        String version = plugin.getDescription().getVersion();

        sender.sendMessage(plugin.getChatUtils().translateColorCodes("&#A855F7&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("     &#A855F7&lᴊ&#AA5BF7&lᴜ&#AF61F7&lꜱ&#B467F7&lᴛ&#B96DF7&lᴄ&#BE73F7&lʜ&#C379F7&lᴀ&#C87FF7&lᴛ   &#FFFFFFᴄᴏᴍᴍᴀɴᴅꜱ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("&#A855F7&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getChatUtils().translateColorCodes(" &#A855F7&l● &#FFFFFFᴄᴏʀᴇ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ᴄʜᴀᴛ ʀᴇʟᴏᴀᴅ &#8E8E8E- &#7F7F7Fʀᴇʟᴏᴀᴅ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ᴄʜᴀᴛ ʜᴇʟᴘ &#8E8E8E- &#7F7F7Fꜱʜᴏᴡ ᴛʜɪꜱ ᴍᴇɴᴜ"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getChatUtils().translateColorCodes(" &#A855F7&l● &#FFFFFFᴄʜᴀᴛ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ᴍꜱɢ <ᴘʟᴀʏᴇʀ> <ᴍᴇꜱꜱᴀɢᴇ> &#8E8E8E- &#7F7F7Fꜱᴇɴᴅ ᴘʀɪᴠᴀᴛᴇ ᴍᴇꜱꜱᴀɢᴇ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ʀᴇᴘʟʏ <ᴍᴇꜱꜱᴀɢᴇ> &#8E8E8E- &#7F7F7Fʀᴇᴘʟʏ ᴛᴏ ʟᴀꜱᴛ ᴍᴇꜱꜱᴀɢᴇ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ɪɢɴᴏʀᴇ <ᴘʟᴀʏᴇʀ> &#8E8E8E- &#7F7F7Fɪɢɴᴏʀᴇ ᴘʟᴀʏᴇʀ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ᴜɴɪɢɴᴏʀᴇ <ᴘʟᴀʏᴇʀ> &#8E8E8E- &#7F7F7Fᴜɴɪɢɴᴏʀᴇ ᴘʟᴀʏᴇʀ"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getChatUtils().translateColorCodes(" &#A855F7&l● &#FFFFFFᴍᴏᴅᴇʀᴀᴛɪᴏɴ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ᴍᴜᴛᴇ <ᴘʟᴀʏᴇʀ> [ᴛɪᴍᴇ] &#8E8E8E- &#7F7F7Fᴍᴜᴛᴇ ᴘʟᴀʏᴇʀ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ᴜɴᴍᴜᴛᴇ <ᴘʟᴀʏᴇʀ> &#8E8E8E- &#7F7F7Fᴜɴᴍᴜᴛᴇ ᴘʟᴀʏᴇʀ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ᴄʜᴀᴛ ᴍᴜᴛᴇᴄʜᴀᴛ &#8E8E8E- &#7F7F7Fᴛᴏɢɢʟᴇ ɢʟᴏʙᴀʟ ᴄʜᴀᴛ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("   &#C084FC/ᴄʟᴇᴀʀᴄʜᴀᴛ &#8E8E8E- &#7F7F7Fᴄʟᴇᴀʀ ᴄʜᴀᴛ"));
        sender.sendMessage("");

        sender.sendMessage(plugin.getChatUtils().translateColorCodes(" &#8E8E8Eᴠᴇʀꜱɪᴏɴ: &#4BC0F1" + version + " &#8E8E8E| &#FFFFFFᴍᴀᴅᴇ ᴡɪᴛʜ &#FF6B6B❤ &#FFFFFFʙʏ &#4BC0F1ᴍᴇʏʙᴀ"));
        sender.sendMessage(plugin.getChatUtils().translateColorCodes("&#A855F7&l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage("");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("justchat.reload")) suggestions.add("reload");
            if (sender.hasPermission("justchat.help")) suggestions.add("help");
            if (sender.hasPermission("justchat.mutechat")) suggestions.add("mutechat");
        }

        for (String s : suggestions) {
            if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                completions.add(s);
            }
        }

        return completions;
    }
}
