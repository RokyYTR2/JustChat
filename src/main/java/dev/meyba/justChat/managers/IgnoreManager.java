package dev.meyba.justChat.managers;

import dev.meyba.justChat.JustChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IgnoreManager {
    private final JustChat plugin;
    private final Map<UUID, Set<UUID>> ignoreMap = new HashMap<>();
    private File ignoreFile;
    private FileConfiguration ignoreConfig;

    public IgnoreManager(JustChat plugin) {
        this.plugin = plugin;
        loadIgnores();
    }

    public void loadIgnores() {
        ignoreFile = new File(plugin.getDataFolder(), "ignores.yml");
        if (!ignoreFile.exists()) {
            try {
                ignoreFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create ignores.yml: " + e.getMessage());
            }
        }
        ignoreConfig = YamlConfiguration.loadConfiguration(ignoreFile);

        ignoreMap.clear();
        for (String key : ignoreConfig.getKeys(false)) {
            UUID ignorer = UUID.fromString(key);
            List<String> ignoredList = ignoreConfig.getStringList(key);
            Set<UUID> ignoredSet = new HashSet<>();
            for (String ignored : ignoredList) {
                ignoredSet.add(UUID.fromString(ignored));
            }
            ignoreMap.put(ignorer, ignoredSet);
        }
    }

    public void saveIgnores() {
        for (Map.Entry<UUID, Set<UUID>> entry : ignoreMap.entrySet()) {
            List<String> ignoredList = new ArrayList<>();
            for (UUID ignored : entry.getValue()) {
                ignoredList.add(ignored.toString());
            }
            ignoreConfig.set(entry.getKey().toString(), ignoredList);
        }

        try {
            ignoreConfig.save(ignoreFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save ignores.yml: " + e.getMessage());
        }
    }

    public void ignorePlayer(UUID ignorer, UUID ignored) {
        ignoreMap.computeIfAbsent(ignorer, k -> new HashSet<>()).add(ignored);
        saveIgnores();
    }

    public void unignorePlayer(UUID ignorer, UUID ignored) {
        Set<UUID> ignoredSet = ignoreMap.get(ignorer);
        if (ignoredSet != null) {
            ignoredSet.remove(ignored);
            if (ignoredSet.isEmpty()) {
                ignoreMap.remove(ignorer);
            }
            saveIgnores();
        }
    }

    public boolean isIgnoring(UUID ignorer, UUID ignored) {
        Set<UUID> ignoredSet = ignoreMap.get(ignorer);
        return ignoredSet != null && ignoredSet.contains(ignored);
    }

    public Set<UUID> getIgnoredPlayers(UUID player) {
        return ignoreMap.getOrDefault(player, new HashSet<>());
    }

    public int getIgnoreCount(UUID player) {
        Set<UUID> ignored = ignoreMap.get(player);
        return ignored != null ? ignored.size() : 0;
    }
}
