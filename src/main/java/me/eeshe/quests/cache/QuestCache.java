package me.eeshe.quests.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import me.eeshe.quests.model.quests.ExploringQuest;
import me.eeshe.quests.model.quests.KillingQuest;
import me.eeshe.quests.model.quests.MiningQuest;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.model.quests.RunningQuest;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

public class QuestCache extends ConcurrentHashMapCache<String, Quest>
    implements MiningQuestCache, KillingQuestCache, ExploringQuestCache {
  private final ConcurrentHashMapCache<Material, Set<MiningQuest>> miningQuestCache =
      new ConcurrentHashMapCache<>();
  private final ConcurrentHashMapCache<EntityType, Set<KillingQuest>> killingQuestCache =
      new ConcurrentHashMapCache<>();
  private final ConcurrentHashMapCache<Biome, Set<ExploringQuest>> exploringQuestCache =
      new ConcurrentHashMapCache<>();
  private final ConcurrentHashMapCache<String, RunningQuest> runningQuestCache =
      new ConcurrentHashMapCache<>();

  @Override
  public void put(String key, Quest value) {
    super.put(key, value);
    if (value instanceof RunningQuest runningQuest) {
      runningQuestCache.put(key, runningQuest);
    } else if (value instanceof MiningQuest miningQuest) {
      addToCache(miningQuest, MiningQuest::getTarget, miningQuestCache);
    } else if (value instanceof KillingQuest killingQuest) {
      addToCache(killingQuest, KillingQuest::getTarget, killingQuestCache);
    } else if (value instanceof ExploringQuest exploringQuest) {
      addToCache(exploringQuest, ExploringQuest::getTarget, exploringQuestCache);
    }
  }

  @Override
  public void clear() {
    super.clear();
    runningQuestCache.clear();
    miningQuestCache.clear();
    killingQuestCache.clear();
    exploringQuestCache.clear();
  }

  private <T extends Quest, K> void addToCache(
      T quest, Function<T, K> targetExtractor, ConcurrentHashMapCache<K, Set<T>> cache) {
    K key = targetExtractor.apply(quest);
    Set<T> list = cache.getOrDefault(key, new HashSet<>());
    list.add(quest);

    cache.put(key, list);
  }

  @Override
  public Set<MiningQuest> getMiningQuests(Material material) {
    return miningQuestCache.get(material);
  }

  @Override
  public Set<KillingQuest> getKillingQuests(EntityType target) {
    return killingQuestCache.get(target);
  }

  @Override
  public Set<ExploringQuest> getExploringQuests(Biome target) {
    return exploringQuestCache.get(target);
  }

  public Set<RunningQuest> getRunningQuests() {
    return new HashSet<>(runningQuestCache.getValues());
  }
}
