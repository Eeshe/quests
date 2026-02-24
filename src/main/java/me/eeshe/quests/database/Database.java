package me.eeshe.quests.database;

import org.bukkit.plugin.Plugin;

public interface Database {

  void connect();

  void disconnect();

  Plugin getPlugin();
}
