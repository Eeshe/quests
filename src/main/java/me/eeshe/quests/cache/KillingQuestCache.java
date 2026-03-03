package me.eeshe.quests.cache;

import java.util.List;
import me.eeshe.quests.model.quests.KillingQuest;
import org.bukkit.entity.EntityType;

public interface KillingQuestCache {

  List<KillingQuest> getKillingQuests(EntityType target);
}
