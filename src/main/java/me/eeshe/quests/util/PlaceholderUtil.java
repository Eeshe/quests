package me.eeshe.quests.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderUtil {

  public static String formatPlaceholders(OfflinePlayer player, String text) {
    if (player != null) {
      text = text.replace("%player%", player.getName());
    }
    if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      return text;
    }
    return PlaceholderAPI.setPlaceholders(player, text);
  }
}
