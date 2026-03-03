package me.eeshe.quests.cache;

import java.util.Set;
import me.eeshe.quests.model.quests.ExploringQuest;
import org.bukkit.block.Biome;

public interface ExploringQuestCache {

  Set<ExploringQuest> getExploringQuests(Biome target);
}
