package dev.meyba.justChat.commands;

import dev.meyba.justChat.JustChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MsgCommand implements CommandExecutor, TabCompleter {
    private final JustChat plugin;

    public MsgCommand(JustChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("not-a-player")));
            return true;
        }

        if (!player.hasPermission("justchat.msg")) {
            player.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("no-permission")));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getChatUtils().formatMessage("&cUsage: /msg <player> <message>"));
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null) {
            player.sendMessage(plugin.getChatUtils().formatMessage(
                    plugin.getChatUtils().getMessage("player-not-found").replace("%player%", args[0])));
            return true;
        }

        if (player.getUniqueId().equals(receiver.getUniqueId())) {
            player.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("cannot-message-self")));
            return true;
        }

        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        if (!plugin.getMessageManager().sendPrivateMessage(player, receiver, message)) {
            player.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("player-ignoring-you")));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList()));
        }

        return completions;
    }
}