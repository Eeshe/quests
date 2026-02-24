package me.eeshe.quests.config;

import me.eeshe.quests.database.DatabaseSettings;

public interface PluginConfig extends Config {

  @Override
  default void writeDefaults() {
    writeDefaultDatabaseSettings();
    save();
  }

  void writeDefaultDatabaseSettings();

  DatabaseSettings getDatabaseSettings();
}
