package me.eeshe.quests.model.questplayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.repository.QuestPlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class QuestPlayer implements IQuestPlayer {
  private final UUID uuid;
  private final Set<String> completedQuestIds;
  private final QuestPlayerRepository questPlayerRepository;
  private final ConcurrentHashMap<String, Integer> questProgress;

  public QuestPlayer(OfflinePlayer player, QuestPlayerRepository questPlayerRepository) {
    this.uuid = player.getUniqueId();
    this.completedQuestIds = new HashSet<>();
    this.questProgress = new ConcurrentHashMap<>();
    this.questPlayerRepository = questPlayerRepository;
  }

  public QuestPlayer(
      OfflinePlayer player,
      Set<String> completedQuestIds,
      ConcurrentHashMap<String, Integer> questProgress,
      QuestPlayerRepository questPlayerRepository) {
    this.uuid = player.getUniqueId();
    this.completedQuestIds = completedQuestIds;
    this.questProgress = questProgress;
    this.questPlayerRepository = questPlayerRepository;
  }

  @Override
  public int getQuestProgress(Quest quest) {
    return questProgress.getOrDefault(quest.getId(), 0);
  }

  @Override
  public ConcurrentHashMap<String, Integer> getQuestProgress() {
    return questProgress;
  }

  @Override
  public void increaseQuestProgress(Quest quest, int progressIncrease) {
    if (hasCompletedQuest(quest)) {
      return;
    }
    setQuestProgress(quest, getQuestProgress(quest) + progressIncrease);
  }

  @Override
  public void setQuestProgress(Quest quest, int progress) {
    final int questGoal = quest.getGoal();
    final int newProgress = Math.min(questGoal, progress);
    questProgress.put(quest.getId(), newProgress);

    if (getQuestProgress(quest) < questGoal) {
      return;
    }
    completeQuest(quest);
  }

  @Override
  public void completeQuest(Quest quest) {
    if (quest == null) {
      return;
    }
    completedQuestIds.add(quest.getId());
    saveData();
  }

  @Override
  public void resetQuest(Quest quest) {
    if (quest == null) {
      return;
    }
    questProgress.put(quest.getId(), 0);
    completedQuestIds.remove(quest.getId());
    saveData();
  }

  @Override
  public void saveData() {
    questPlayerRepository.save(this);
  }

  @Override
  public UUID getUuid() {
    return uuid;
  }

  @Override
  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  @Override
  public Set<String> getCompletedQuestIds() {
    return completedQuestIds;
  }

  @Override
  public boolean hasCompletedQuest(Quest quest) {
    return completedQuestIds.contains(quest.getId());
  }
}
