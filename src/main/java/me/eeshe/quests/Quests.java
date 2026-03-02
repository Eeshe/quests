package me.eeshe.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.eeshe.quests.config.Config;
import me.eeshe.quests.config.Message;
import me.eeshe.quests.config.MessageConfig;
import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.config.Sound;
import me.eeshe.quests.config.SoundConfig;
import me.eeshe.quests.config.registry.MessageRegistry;
import me.eeshe.quests.config.registry.SoundRegistry;
import me.eeshe.quests.config.yaml.YAMLMessageConfig;
import me.eeshe.quests.config.yaml.YAMLPluginConfig;
import me.eeshe.quests.config.yaml.YAMLSoundConfig;
import me.eeshe.quests.database.Database;
import me.eeshe.quests.database.MongoDatabase;
import me.eeshe.quests.listeners.PlayerConnectionListener;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.repository.QuestRepository;
import me.eeshe.quests.repository.Repository;
import me.eeshe.quests.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Quests extends JavaPlugin {
  private final List<Config> configs = new ArrayList<>();
  private final List<Repository> repositories = new ArrayList<>();
  private final List<Listener> listeners = new ArrayList<>();

  private PluginConfig pluginConfig;

  private Database database;

  private QuestRepository questRepository;
  private QuestPlayerRepository questPlayerRepository;

  public static Quests getInstance() {
    return getPlugin(Quests.class);
  }

  @Override
  public void onEnable() {
    LogUtil.initialize(this);

    loadConfigs();
    connectDatabase()
        .whenComplete(
            (unused, throwable) -> {
              if (!database.isConnected()) {
                LogUtil.error("Could not connect to database. Plugin will not load.");
                getServer().getPluginManager().disablePlugin(this);
                return;
              }
              loadRepositories();
              registerListeners();
              for (int i = 0; i < 10; i++) {
                MessageRegistry.TEST.send(Bukkit.getConsoleSender());
                SoundRegistry.TEST.play(Bukkit.getConsoleSender());
              }
            });
  }

  private void loadConfigs() {
    this.pluginConfig = new YAMLPluginConfig(this);
    final MessageConfig messageConfig = new YAMLMessageConfig(this);
    final SoundConfig soundConfig = new YAMLSoundConfig(this);

    configs.addAll(List.of(pluginConfig, messageConfig, soundConfig));

    for (Config config : configs) {
      config.load();
    }
    Message.setMessageConfig(messageConfig);
    Sound.setSoundConfig(soundConfig);
  }

  private CompletableFuture<Void> connectDatabase() {
    this.database = new MongoDatabase(this);

    return database.connect();
  }

  private void loadRepositories() {
    repositories.clear();

    this.questRepository = new QuestRepository(this);
    this.questPlayerRepository = new QuestPlayerRepository(this);
    repositories.addAll(List.of(questRepository, questPlayerRepository));

    database.setRepositories(questRepository, questPlayerRepository);

    for (Repository repository : repositories) {
      repository.load();
    }
  }

  private void registerListeners() {
    listeners.addAll(List.of(new PlayerConnectionListener(questPlayerRepository)));

    final PluginManager pluginManager = getServer().getPluginManager();
    for (Listener listener : listeners) {
      pluginManager.registerEvents(listener, this);
    }
  }

  @Override
  public void onDisable() {
    unloadConfigs();
    disconnectDatabase(false);
    unloadRepositories();
    unregisterListeners();
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

  private void unregisterListeners() {
    for (Listener listener : listeners) {
      HandlerList.unregisterAll(listener);
    }
  }

  public void reload() {
    unloadConfigs();
    disconnectDatabase(true);
    unloadRepositories();
    unregisterListeners();

    loadConfigs();
    connectDatabase();
    loadRepositories();
    registerListeners();
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

  public QuestPlayerRepository getQuestPlayerRepository() {
    return questPlayerRepository;
  }
}
