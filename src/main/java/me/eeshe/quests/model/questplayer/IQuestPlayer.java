package me.eeshe.quests.model.questplayer;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.eeshe.quests.model.quests.Quest;
import org.bukkit.entity.Player;

public interface IQuestPlayer {

  void increaseQuestProgress(Quest quest, int increase);

  void setQuestProgress(Quest quest, int progress);

  void completeQuest(Quest quest);

  void resetQuest(Quest quest);

  void saveData();

  UUID getUuid();

  Player getPlayer();

  int getQuestProgress(Quest quest);

  ConcurrentHashMap<String, Integer> getQuestProgress();

  Set<String> getCompletedQuestIds();

  boolean hasCompletedQuest(Quest quest);
}
