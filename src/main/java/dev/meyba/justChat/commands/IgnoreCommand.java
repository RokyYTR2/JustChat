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

public class IgnoreCommand implements CommandExecutor, TabCompleter {
    private final JustChat plugin;

    public IgnoreCommand(JustChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("not-a-player")));
            return true;
        }

        if (!player.hasPermission("justchat.ignore")) {
            player.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("no-permission")));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getChatUtils().formatMessage("&cUsage: /ignore <player>"));
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(plugin.getChatUtils().formatMessage(
                    plugin.getChatUtils().getMessage("player-not-found").replace("%player%", args[0])));
            return true;
        }

        if (player.getUniqueId().equals(targetPlayer.getUniqueId())) {
            player.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("cannot-ignore-self")));
            return true;
        }

        if (plugin.getIgnoreManager().isIgnoring(player.getUniqueId(), targetPlayer.getUniqueId())) {
            player.sendMessage(plugin.getChatUtils().formatMessage(
                    plugin.getChatUtils().getMessage("already-ignoring").replace("%player%", targetPlayer.getName())));
            return true;
        }

        plugin.getIgnoreManager().ignorePlayer(player.getUniqueId(), targetPlayer.getUniqueId());
        player.sendMessage(plugin.getChatUtils().formatMessage(
                plugin.getChatUtils().getMessage("ignore-success").replace("%player%", targetPlayer.getName())));

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