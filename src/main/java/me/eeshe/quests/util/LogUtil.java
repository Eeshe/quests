package me.eeshe.quests.util;

import org.bukkit.plugin.Plugin;

public class LogUtil {
  private static Plugin PLUGIN;

  public static void initialize(Plugin plugin) {
    if (PLUGIN != null) {
      return;
    }
    PLUGIN = plugin;
  }

  public static void info(String text) {
    PLUGIN.getLogger().info(text);
  }

  public static void warning(String text) {
    PLUGIN.getLogger().warning(text);
  }

  public static void error(String text) {
    PLUGIN.getLogger().severe(text);
  }
}
