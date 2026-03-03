package me.eeshe.quests.model.questplayer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.eeshe.quests.model.quests.Quest;
import org.bukkit.entity.Player;

public interface IQuestPlayer {

  void increaseQuestProgress(Quest quest, double increase);

  void setQuestProgress(Quest quest, double progress);

  void completeQuest(Quest quest);

  void resetQuest(Quest quest);

  void saveData();

  UUID getUuid();

  Player getPlayer();

  double getQuestProgress(Quest quest);

  ConcurrentHashMap<String, Double> getQuestProgress();

  Set<String> getCompletedQuestIds();

  boolean hasCompletedQuest(Quest quest);
}
