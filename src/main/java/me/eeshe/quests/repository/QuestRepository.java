package me.eeshe.quests.repository;

import java.util.ArrayList;
import java.util.List;
import me.eeshe.quests.Quests;
import me.eeshe.quests.cache.QuestCache;
import me.eeshe.quests.model.quests.ExploringQuest;
import me.eeshe.quests.model.quests.KillingQuest;
import me.eeshe.quests.model.quests.MiningQuest;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.model.quests.RunningQuest;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

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

  public List<MiningQuest> getMiningQuests(Material material) {
    return cache.getMiningQuests(material);
  }

  public List<KillingQuest> getKillingQuests(EntityType entityType) {
    return cache.getKillingQuests(entityType);
  }

  public List<ExploringQuest> getExploringQuests(Biome biome) {
    return cache.getExploringQuests(biome);
  }

  public List<RunningQuest> getRunningQuests() {}
}
