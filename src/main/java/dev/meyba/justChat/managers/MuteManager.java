package dev.meyba.justChat.managers;

import dev.meyba.justChat.JustChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MuteManager {
    private final JustChat plugin;
    private final File mutesFile;
    private final FileConfiguration mutesConfig;
    private final Map<UUID, Long> mutedPlayers = new HashMap<>();

    public MuteManager(JustChat plugin) {
        this.plugin = plugin;
        this.mutesFile = new File(plugin.getDataFolder(), "mutes.yml");
        if (!mutesFile.exists()) {
            plugin.saveResource("mutes.yml", false);
        }
        this.mutesConfig = YamlConfiguration.loadConfiguration(mutesFile);
        loadMutes();
    }

    public void loadMutes() {
        mutedPlayers.clear();
        if (mutesConfig.getConfigurationSection("mutes") != null) {
            for (String uuidString : mutesConfig.getConfigurationSection("mutes").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                long expiry = mutesConfig.getLong("mutes." + uuidString);
                mutedPlayers.put(uuid, expiry);
            }
        }
    }

    public void saveMutes() {
        try {
            mutesConfig.set("mutes", null);
            for (Map.Entry<UUID, Long> entry : mutedPlayers.entrySet()) {
                mutesConfig.set("mutes." + entry.getKey().toString(), entry.getValue());
            }
            mutesConfig.save(mutesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save mutes.yml!");
            e.printStackTrace();
        }
    }

    public void mutePlayer(UUID uuid, long durationMillis) {
        long expiryTime = (durationMillis == -1) ? -1 : System.currentTimeMillis() + durationMillis;
        mutedPlayers.put(uuid, expiryTime);
        saveMutes();
    }

    public void unmutePlayer(UUID uuid) {
        mutedPlayers.remove(uuid);
        saveMutes();
    }

    public boolean isMuted(UUID uuid) {
        if (!mutedPlayers.containsKey(uuid)) {
            return false;
        }
        long expiryTime = mutedPlayers.get(uuid);
        if (expiryTime == -1) {
            return true;
        }
        if (System.currentTimeMillis() < expiryTime) {
            return true;
        } else {
            unmutePlayer(uuid);
            return false;
        }
    }

    public long parseDuration(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return -1;
        }
        try {
            char unit = durationStr.charAt(durationStr.length() - 1);
            long value = Long.parseLong(durationStr.substring(0, durationStr.length() - 1));
            switch (Character.toLowerCase(unit)) {
                case 's':
                    return TimeUnit.SECONDS.toMillis(value);
                case 'm':
                    return TimeUnit.MINUTES.toMillis(value);
                case 'h':
                    return TimeUnit.HOURS.toMillis(value);
                case 'd':
                    return TimeUnit.DAYS.toMillis(value);
                default:
                    return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}