package me.eeshe.quests.cache;

import java.util.Set;
import me.eeshe.quests.model.quests.KillingQuest;
import org.bukkit.entity.EntityType;

public interface KillingQuestCache {

  Set<KillingQuest> getKillingQuests(EntityType target);
}
