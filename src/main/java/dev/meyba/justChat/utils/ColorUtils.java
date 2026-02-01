package dev.meyba.justChat.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String translateColorCodes(String message) {
        if (message == null) {
            return null;
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            try {
                ChatColor color = ChatColor.of("#" + hexCode);
                matcher.appendReplacement(buffer, color.toString());
            } catch (IllegalArgumentException e) {
                matcher.appendReplacement(buffer, matcher.group(0));
            }
        }
        matcher.appendTail(buffer);

        String result = buffer.toString();
        result = ChatColor.translateAlternateColorCodes('&', result);

        return result;
    }
}