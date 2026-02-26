package me.eeshe.quests.config;

import java.util.List;
import me.eeshe.quests.database.DatabaseSettings;
import me.eeshe.quests.model.quests.Quest;

public interface PluginConfig extends Config {

  @Override
  default void writeDefaults() {
    writeDefaultDatabaseSettings();
    writeDefaultQuests();
    save();
  }

  void writeDefaultDatabaseSettings();

  void writeDefaultQuests();

  DatabaseSettings getDatabaseSettings();

  List<Quest> getQuests();
}
