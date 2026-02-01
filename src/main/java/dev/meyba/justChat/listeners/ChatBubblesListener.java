package dev.meyba.justChat.listeners;

import dev.meyba.justChat.JustChat;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public class ChatBubblesListener implements Listener {
    private final JustChat plugin;
    private final Map<UUID, TextDisplay> activeBubbles;
    private final Map<UUID, List<String>> bubbleMessages;
    private final Map<UUID, BukkitTask> removalTasks;

    public ChatBubblesListener(JustChat plugin) {
        this.plugin = plugin;
        this.activeBubbles = new HashMap<>();
        this.bubbleMessages = new HashMap<>();
        this.removalTasks = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("chat-bubbles.enabled")) return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (plugin.getMuteManager().isMuted(player.getUniqueId())) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            updateBubble(player, message);
        });
    }

    private void updateBubble(Player player, String newMessage) {
        UUID uuid = player.getUniqueId();
        List<String> messages = bubbleMessages.computeIfAbsent(uuid, k -> new ArrayList<>());

        messages.add(newMessage);
        int maxLines = plugin.getConfig().getInt("chat-bubbles.max-lines", 3);
        if (messages.size() > maxLines) {
            messages.remove(0);
        }

        String combinedMessage = String.join("\n", messages);
        TextDisplay display = activeBubbles.get(uuid);

        if (display == null || display.isDead()) {
            display = spawnBubble(player, combinedMessage);
            activeBubbles.put(uuid, display);
        } else {
            display.setText(combinedMessage);
        }

        BukkitTask existingTask = removalTasks.remove(uuid);
        if (existingTask != null) {
            existingTask.cancel();
        }

        long duration = plugin.getConfig().getLong("chat-bubbles.duration", 100L);
        BukkitTask newTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            removeBubble(uuid);
        }, duration);

        removalTasks.put(uuid, newTask);
    }

    private TextDisplay spawnBubble(Player player, String message) {
        double heightOffset = plugin.getConfig().getDouble("chat-bubbles.height-offset");
        Location location = player.getLocation().add(0, heightOffset, 0);
        TextDisplay display = player.getWorld().spawn(location, TextDisplay.class);

        display.setText(message);
        display.setBillboard(Display.Billboard.CENTER);
        display.setSeeThrough(false);
        display.setShadowed(true);
        display.setBackgroundColor(Color.fromARGB(100, 0, 0, 0));

        player.addPassenger(display);

        display.setTransformation(new Transformation(
                new Vector3f(0, (float) 2.0, 0),
                new AxisAngle4f(),
                new Vector3f(1, 1, 1),
                new AxisAngle4f()
        ));

        return display;
    }

    private void removeBubble(UUID playerUuid) {
        BukkitTask task = removalTasks.remove(playerUuid);
        if (task != null) {
            task.cancel();
        }

        bubbleMessages.remove(playerUuid);
        TextDisplay display = activeBubbles.remove(playerUuid);
        if (display != null) {
            display.remove();
        }
    }

    public void cleanup() {
        for (BukkitTask task : removalTasks.values()) {
            task.cancel();
        }
        removalTasks.clear();

        for (TextDisplay display : activeBubbles.values()) {
            display.remove();
        }
        activeBubbles.clear();
        bubbleMessages.clear();
    }
}