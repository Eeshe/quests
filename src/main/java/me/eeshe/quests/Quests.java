package me.eeshe.quests;

import java.util.ArrayList;
import java.util.List;
import me.eeshe.quests.config.Config;
import me.eeshe.quests.config.MessageConfig;
import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.config.yaml.YAMLMessageConfig;
import me.eeshe.quests.config.yaml.YAMLPluginConfig;
import me.eeshe.quests.database.Database;
import me.eeshe.quests.database.MongoDatabase;
import me.eeshe.quests.model.Message;
import me.eeshe.quests.repository.QuestRepository;
import me.eeshe.quests.repository.Repository;
import me.eeshe.quests.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Quests extends JavaPlugin {
  private final List<Config> configs = new ArrayList<>();
  private final List<Repository> repositories = new ArrayList<>();

  private PluginConfig pluginConfig;

  private Database database;

  private QuestRepository questRepository;

  public void reload() {
    unloadConfigs();
    disconnectDatabase(true);
    unloadRepositories();

    loadConfigs();
    connectDatabase();
    loadRepositories();
  }

  @Override
  public void onEnable() {
    LogUtil.initialize(this);

    loadConfigs();
    connectDatabase();
    loadRepositories();
  }

  private void loadConfigs() {
    this.pluginConfig = new YAMLPluginConfig(this);
    final MessageConfig messageConfig = new YAMLMessageConfig(this);

    configs.addAll(List.of(pluginConfig, messageConfig));

    for (Config config : configs) {
      config.load();
    }
    Message.setMessageConfig(messageConfig);
  }

  private void connectDatabase() {
    this.database = new MongoDatabase(this);

    Bukkit.getScheduler().runTaskAsynchronously(this, database::connect);
  }

  private void loadRepositories() {
    repositories.clear();

    this.questRepository = new QuestRepository(this);
    repositories.addAll(List.of(questRepository));

    for (Repository repository : repositories) {
      repository.load();
    }
  }

  @Override
  public void onDisable() {
    unloadConfigs();
    disconnectDatabase(false);
    unloadRepositories();
  }

  private void unloadConfigs() {
    configs.clear();
  }

  private void disconnectDatabase(boolean async) {
    if (!async) {
      database.disconnect();
      return;
    }
    Bukkit.getScheduler().runTaskAsynchronously(this, database::disconnect);
  }

  private void unloadRepositories() {
    for (Repository manager : repositories) {
      manager.unload();
    }
    repositories.clear();
  }

  public PluginConfig getPluginConfig() {
    return pluginConfig;
  }

  public Database getDatabase() {
    return database;
  }

  public QuestRepository getQuestRepository() {
    return questRepository;
  }
}
