package me.eeshe.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.eeshe.quests.commands.CommandCompleter;
import me.eeshe.quests.commands.CommandQuests;
import me.eeshe.quests.commands.CommandRunner;
import me.eeshe.quests.commands.PluginCommand;
import me.eeshe.quests.commands.questadmin.CommandQuestsAdmin;
import me.eeshe.quests.config.Config;
import me.eeshe.quests.config.Menu;
import me.eeshe.quests.config.MenuConfig;
import me.eeshe.quests.config.Message;
import me.eeshe.quests.config.MessageConfig;
import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.config.Sound;
import me.eeshe.quests.config.SoundConfig;
import me.eeshe.quests.config.yaml.YAMLMenuConfig;
import me.eeshe.quests.config.yaml.YAMLMessageConfig;
import me.eeshe.quests.config.yaml.YAMLPluginConfig;
import me.eeshe.quests.config.yaml.YAMLSoundConfig;
import me.eeshe.quests.database.Database;
import me.eeshe.quests.database.MongoDatabase;
import me.eeshe.quests.listeners.MenuListener;
import me.eeshe.quests.listeners.PlayerConnectionListener;
import me.eeshe.quests.listeners.QuestListener;
import me.eeshe.quests.placeholderapi.QuestsExpansion;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.repository.QuestRepository;
import me.eeshe.quests.repository.Repository;
import me.eeshe.quests.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Quests extends JavaPlugin {
  private final Map<String, PluginCommand> commands = new HashMap<>();

  private final List<Config> configs = new ArrayList<>();
  private final List<Repository> repositories = new ArrayList<>();
  private final List<Listener> listeners = new ArrayList<>();
  private final List<PlaceholderExpansion> placeholderExpansions = new ArrayList<>();

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
              if (throwable != null) {
                LogUtil.error(
                    "Could not connect to database. Plugin will not load. Error message: "
                        + throwable.getMessage());
                getServer().getPluginManager().disablePlugin(this);
                return;
              }
              registerCommands();
              loadRepositories();
              registerListeners();
              getServer().getScheduler().runTask(this, this::registerPlaceholderExpansions);
            });
  }

  private void loadConfigs() {
    this.pluginConfig = new YAMLPluginConfig(this);
    final MessageConfig messageConfig = new YAMLMessageConfig(this);
    final SoundConfig soundConfig = new YAMLSoundConfig(this);
    final MenuConfig menuConfig = new YAMLMenuConfig(this);

    configs.addAll(List.of(pluginConfig, messageConfig, soundConfig, menuConfig));

    for (Config config : configs) {
      config.load();
    }
    Message.setMessageConfig(messageConfig);
    Sound.setSoundConfig(soundConfig);
    Menu.setMenuConfig(menuConfig);
  }

  private CompletableFuture<Void> connectDatabase() {
    this.database = new MongoDatabase(this);

    return database.connect();
  }

  private void registerCommands() {
    final CommandExecutor commandExecutor = new CommandRunner(this);
    final TabCompleter tabCompleter = new CommandCompleter(this);
    final List<PluginCommand> pluginCommands =
        List.of(new CommandQuests(this), new CommandQuestsAdmin(this));

    for (PluginCommand pluginCommand : pluginCommands) {
      org.bukkit.command.PluginCommand bukkitPluginCommand =
          pluginCommand.getPlugin().getServer().getPluginCommand(pluginCommand.getName());
      if (bukkitPluginCommand == null) continue;

      this.commands.put(pluginCommand.getName(), pluginCommand);
      bukkitPluginCommand.setExecutor(commandExecutor);
      bukkitPluginCommand.setTabCompleter(tabCompleter);
    }
  }

  private void loadRepositories() {
    repositories.clear();

    this.questRepository = new QuestRepository(this);
    this.questPlayerRepository = new QuestPlayerRepository(this);
    repositories.addAll(List.of(questRepository, questPlayerRepository));

    database.setRepositories(questPlayerRepository);

    for (Repository repository : repositories) {
      repository.load();
    }
  }

  private void registerListeners() {
    listeners.addAll(
        List.of(
            new PlayerConnectionListener(questPlayerRepository),
            new MenuListener(this),
            new QuestListener(this)));

    final PluginManager pluginManager = getServer().getPluginManager();
    for (Listener listener : listeners) {
      pluginManager.registerEvents(listener, this);
    }
  }

  private void registerPlaceholderExpansions() {
    if (!getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      return;
    }
    placeholderExpansions.clear();
    placeholderExpansions.addAll(List.of(new QuestsExpansion(this)));

    for (PlaceholderExpansion placeholderExpansion : placeholderExpansions) {
      placeholderExpansion.register();
    }
  }

  @Override
  public void onDisable() {
    unloadConfigs();
    disconnectDatabase(false);
    unloadRepositories();
    unregisterListeners();
    unregisterPlaceholderExpansions();
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
    listeners.clear();
  }

  private void unregisterPlaceholderExpansions() {
    for (PlaceholderExpansion placeholderExpansion : placeholderExpansions) {
      placeholderExpansion.unregister();
    }
    placeholderExpansions.clear();
  }

  public void reload() {
    unloadConfigs();
    disconnectDatabase(true);
    unloadRepositories();
    unregisterListeners();
    unregisterPlaceholderExpansions();

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
              registerPlaceholderExpansions();
            });
  }

  public Map<String, PluginCommand> getCommands() {
    return commands;
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
