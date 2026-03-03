package me.eeshe.quests.util;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

  public static ItemStack formatPlaceholders(
      OfflinePlayer player, ItemStack item, Map<String, String> placeholders) {
    if (item == null) {
      return null;
    }
    item = item.clone();

    final ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      meta.setDisplayName(formatPlaceholders(player, meta.getDisplayName(), placeholders));
    }
    final List<String> lore = meta.getLore();
    if (lore != null) {
      final ListIterator<String> loreIterator = lore.listIterator();
      while (loreIterator.hasNext()) {
        String loreLine = loreIterator.next();
        loreLine = formatPlaceholders(player, loreLine, placeholders);

        loreIterator.set(loreLine);
      }
      meta.setLore(lore);
    }
    item.setItemMeta(meta);

    return item;
  }

  public static String formatPlaceholders(String text, Map<String, String> placeholders) {
    return formatPlaceholders(null, text, placeholders);
  }

  public static String formatPlaceholders(
      OfflinePlayer player, String text, Map<String, String> placeholders) {
    final boolean isPlaceholderAPIEnabled =
        Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    for (Entry<String, String> entry : placeholders.entrySet()) {
      text = text.replace(entry.getKey(), entry.getValue());
      if (isPlaceholderAPIEnabled) {
        text = PlaceholderAPI.setPlaceholders(player, text);
      }
    }
    return StringUtil.formatColor(text);
  }
}
