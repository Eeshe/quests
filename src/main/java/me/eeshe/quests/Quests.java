package me.eeshe.quests;

import java.util.ArrayList;
import java.util.List;
import me.eeshe.quests.config.Config;
import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.config.yaml.YAMLPluginConfig;
import me.eeshe.quests.database.Database;
import me.eeshe.quests.database.MongoDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Quests extends JavaPlugin {
  private final List<Config> configs = new ArrayList<>();

  private PluginConfig pluginConfig;
  private Database database;

  @Override
  public void onEnable() {
    loadConfigs();
    connectDatabase();
  }

  private void loadConfigs() {
    this.pluginConfig = new YAMLPluginConfig(this);
    configs.addAll(List.of(pluginConfig));

    for (Config config : configs) {
      config.load();
    }
  }

  private void connectDatabase() {
    this.database = new MongoDatabase(this);

    Bukkit.getScheduler().runTaskAsynchronously(this, database::connect);
  }

  @Override
  public void onDisable() {
    disconnectDatabase(false);
  }

  private void disconnectDatabase(boolean async) {
    if (!async) {
      database.disconnect();
      return;
    }
    Bukkit.getScheduler().runTaskAsynchronously(this, database::disconnect);
  }

  public PluginConfig getPluginConfig() {
    return pluginConfig;
  }

  public Database getDatabase() {
    return database;
  }
}
