package me.eeshe.quests.model.questplayer;

import java.util.Set;
import java.util.UUID;
import me.eeshe.quests.model.quests.Quest;
import org.bukkit.entity.Player;

public interface IQuestPlayer {

  void increaseCurrentQuestProgress(int increase);

  void setCurrentQuestProgress(int progress);

  void completeCurrentQuest();

  void resetQuest(Quest quest);

  void saveData();

  UUID getUuid();

  Player getPlayer();

  String getCurrentQuestId();

  Quest getCurrentQuest();

  int getCurrentQuestProgress();

  Set<String> getCompletedQuestIds();

  boolean hasCompletedQuest(Quest quest);
}
