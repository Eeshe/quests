package me.eeshe.quests.model.questplayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.repository.QuestRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class QuestPlayer implements IQuestPlayer {
  private final UUID uuid;
  private final Set<String> completedQuestIds;
  private final QuestRepository questRepository;
  private final QuestPlayerRepository questPlayerRepository;

  private String currentQuestId;
  private int currentQuestProgress;

  public QuestPlayer(
      OfflinePlayer player,
      QuestRepository questRepository,
      QuestPlayerRepository questPlayerRepository) {
    this.uuid = player.getUniqueId();
    this.completedQuestIds = new HashSet<>();
    this.currentQuestId = null;
    this.currentQuestProgress = 0;
    this.questRepository = questRepository;
    this.questPlayerRepository = questPlayerRepository;

    completedQuestIds.add("mining");
    currentQuestId = "killing";
    currentQuestProgress = 30;
  }

  public QuestPlayer(
      OfflinePlayer player,
      Set<String> completedQuestIds,
      String currentQuestId,
      int currentQuestProgress,
      QuestRepository questRepository,
      QuestPlayerRepository questPlayerRepository) {
    this.uuid = player.getUniqueId();
    this.completedQuestIds = completedQuestIds;
    this.currentQuestId = currentQuestId;
    this.currentQuestProgress = currentQuestProgress;
    this.questRepository = questRepository;
    this.questPlayerRepository = questPlayerRepository;
  }

  @Override
  public void increaseCurrentQuestProgress(int progressIncrease) {
    setCurrentQuestProgress(getCurrentQuestProgress() + progressIncrease);
  }

  @Override
  public void setCurrentQuestProgress(int progress) {
    final Quest currentQuest = getCurrentQuest();
    if (currentQuest == null) {
      return;
    }
    final int questGoal = currentQuest.getGoal();
    currentQuestProgress = Math.min(questGoal, progress);

    if (currentQuestProgress < questGoal) {
      return;
    }
    completeCurrentQuest();
  }

  @Override
  public void completeCurrentQuest() {
    final Quest currentQuest = getCurrentQuest();
    if (currentQuest == null) {
      return;
    }
    currentQuestId = null;
    currentQuestProgress = 0;
    completedQuestIds.add(currentQuestId);

    saveData();
  }

  @Override
  public void resetQuest(Quest quest) {
    if (quest == null) {
      return;
    }
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
  public String getCurrentQuestId() {
    return currentQuestId;
  }

  @Override
  public Quest getCurrentQuest() {
    if (currentQuestId == null) {
      return null;
    }
    return questRepository.get(currentQuestId);
  }

  @Override
  public int getCurrentQuestProgress() {
    return currentQuestProgress;
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
