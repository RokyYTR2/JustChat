package dev.meyba.justChat.commands;

import dev.meyba.justChat.JustChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand implements CommandExecutor {
    private final JustChat plugin;

    public ReplyCommand(JustChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("not-a-player")));
            return true;
        }

        if (!player.hasPermission("justchat.reply")) {
            player.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("no-permission")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getChatUtils().formatMessage("&cUsage: /reply <message>"));
            return true;
        }

        UUID receiverUUID = plugin.getMessageManager().getRecentMessager(player.getUniqueId());
        if (receiverUUID == null) {
            player.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("no-one-to-reply-to")));
            return true;
        }

        Player receiver = Bukkit.getPlayer(receiverUUID);
        if (receiver == null) {
            player.sendMessage(plugin.getChatUtils().formatMessage(
                    plugin.getChatUtils().getMessage("player-not-found").replace("%player%", "your last contact")));
            return true;
        }

        String message = String.join(" ", args);

        if (!plugin.getMessageManager().sendPrivateMessage(player, receiver, message)) {
            player.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("player-ignoring-you")));
        }

        return true;
    }
}