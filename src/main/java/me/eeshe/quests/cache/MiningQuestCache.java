package me.eeshe.quests.cache;

import java.util.List;
import me.eeshe.quests.model.quests.MiningQuest;
import org.bukkit.Material;

public interface MiningQuestCache {

  List<MiningQuest> getMiningQuests(Material material);
}
