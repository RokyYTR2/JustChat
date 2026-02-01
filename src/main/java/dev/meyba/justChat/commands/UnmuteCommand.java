package dev.meyba.justChat.commands;

import dev.meyba.justChat.JustChat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnmuteCommand implements CommandExecutor, TabCompleter {
    private final JustChat plugin;

    public UnmuteCommand(JustChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("justchat.unmute")) {
            sender.sendMessage(plugin.getChatUtils().formatMessage(plugin.getChatUtils().getMessage("no-permission")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getChatUtils().formatMessage("&cUsage: /unmute <player>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(plugin.getChatUtils().formatMessage(
                    plugin.getChatUtils().getMessage("player-not-found").replace("%player%", args[0])));
            return true;
        }

        if (!plugin.getMuteManager().isMuted(target.getUniqueId())) {
            sender.sendMessage(plugin.getChatUtils().formatMessage(
                    plugin.getChatUtils().getMessage("player-not-muted").replace("%player%", target.getName())));
            return true;
        }

        plugin.getMuteManager().unmutePlayer(target.getUniqueId());

        sender.sendMessage(plugin.getChatUtils().formatMessage(
                plugin.getChatUtils().getMessage("player-unmute-success").replace("%player%", target.getName())));

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