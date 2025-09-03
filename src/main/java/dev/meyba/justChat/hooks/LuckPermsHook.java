package dev.meyba.justChat.hooks;

import dev.meyba.justChat.JustChat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LuckPermsHook {
    private final JustChat plugin;
    private LuckPerms api;

    public LuckPermsHook(JustChat plugin) {
        this.plugin = plugin;
        try {
            api = Bukkit.getServicesManager().load(LuckPerms.class);
        } catch (Exception e) {
            api = null;
        }
    }

    public boolean isEnabled() {
        return api != null;
    }

    public String getPrimaryGroup(Player player) {
        if (!isEnabled()) return "default";
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "default";
        return user.getPrimaryGroup();
    }
}