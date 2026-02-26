package me.eeshe.quests.repository;

import java.util.HashMap;
import java.util.Map;
import me.eeshe.quests.Quests;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.util.LogUtil;

public class QuestRepository implements Repository {
  private final Map<String, Quest> quests = new HashMap<>();

  private final Quests plugin;

  public QuestRepository(Quests plugin) {
    this.plugin = plugin;
  }

  @Override
  public void load() {
    quests.clear();
    for (Quest quest : plugin.getPluginConfig().getQuests()) {
      quests.put(quest.getId(), quest);
    }
    LogUtil.warning("Loaded quests: " + quests.size());
  }

  @Override
  public void unload() {
    quests.clear();
  }
}
