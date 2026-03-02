package me.eeshe.quests.repository;

import java.util.ArrayList;
import java.util.List;
import me.eeshe.quests.Quests;
import me.eeshe.quests.cache.QuestCache;
import me.eeshe.quests.model.quests.Quest;

public class QuestRepository implements Repository {
  private final QuestCache cache;
  private final Quests plugin;

  public QuestRepository(Quests plugin) {
    this.cache = new QuestCache();
    this.plugin = plugin;
  }

  @Override
  public void load() {
    cache.clear();
    for (Quest quest : plugin.getPluginConfig().getQuests()) {
      cache.put(quest.getId(), quest);
    }
  }

  @Override
  public void unload() {
    cache.clear();
  }

  public Quest get(String questId) {
    return cache.get(questId);
  }

  public List<Quest> getAll() {
    return new ArrayList<>(cache.getValues());
  }
}
