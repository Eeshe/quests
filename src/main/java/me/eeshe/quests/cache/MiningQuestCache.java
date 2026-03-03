package me.eeshe.quests.cache;

import java.util.Set;
import me.eeshe.quests.model.quests.MiningQuest;
import org.bukkit.Material;

public interface MiningQuestCache {

  Set<MiningQuest> getMiningQuests(Material material);
}
