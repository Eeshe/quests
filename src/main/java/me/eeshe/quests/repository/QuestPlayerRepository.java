package me.eeshe.quests.repository;

import me.eeshe.quests.Quests;
import me.eeshe.quests.cache.QuestPlayerCache;
import me.eeshe.quests.database.Database;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class QuestPlayerRepository implements Repository {
  private final QuestPlayerCache cache;
  private final Quests plugin;
  private final Database database;

  private BukkitTask dataSaveTask;

  public QuestPlayerRepository(Quests plugin) {
    this.cache = new QuestPlayerCache();
    this.plugin = plugin;
    this.database = plugin.getDatabase();
  }

  @Override
  public void load() {
    cacheOnlinePlayers();
    startDataSaveTask();
  }

  private void cacheOnlinePlayers() {
    cache.clear();
    for (Player online : Bukkit.getOnlinePlayers()) {
      put(online);
    }
  }

  private void startDataSaveTask() {
    this.dataSaveTask =
        Bukkit.getScheduler().runTaskTimer(plugin, this::saveOnlinePlayerData, 100L, 100L);
  }

  private void saveOnlinePlayerData() {
    for (Player online : Bukkit.getOnlinePlayers()) {
      final IQuestPlayer questPlayer = get(online);
      if (questPlayer == null) {
        return;
      }
      questPlayer.saveData();
    }
  }

  @Override
  public void unload() {
    cache.clear();
    stopDataSaveTask();
  }

  private void stopDataSaveTask() {
    if (dataSaveTask == null) {
      return;
    }
    dataSaveTask.cancel();
    dataSaveTask = null;
  }

  public void save(IQuestPlayer questPlayer) {
    database.saveQuestPlayerAsync(questPlayer);
  }

  public IQuestPlayer get(Player player) {
    return cache.get(player.getUniqueId());
  }

  public void put(Player player) {
    database
        .fetchQuestPlayerAsync(player)
        .thenAccept(
            questPlayer -> {
              cache.put(player.getUniqueId(), questPlayer);
            })
        .exceptionally(
            ex -> {
              LogUtil.error("Error fetching QuestPlayer. Error message: " + ex.getMessage());
              return null;
            });
  }

  public void remove(Player player) {
    cache.remove(player.getUniqueId());
  }
}
