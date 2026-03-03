package me.eeshe.quests.cache;

import java.util.List;
import me.eeshe.quests.model.quests.ExploringQuest;
import org.bukkit.block.Biome;

public interface ExploringQuestCache {

  List<ExploringQuest> getExploringQuests(Biome target);
}
