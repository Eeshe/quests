package me.eeshe.quests.model.quests;

import java.util.List;
import me.eeshe.quests.model.rewards.Reward;
import org.bukkit.inventory.ItemStack;

public abstract class Quest {
  private final String id;
  private final ItemStack icon;
  private final int goal;
  private final List<Reward> rewards;

  public Quest(String id, ItemStack icon, int goal, List<Reward> rewards) {
    this.id = id;
    this.icon = icon;
    this.goal = goal;
    this.rewards = rewards;
  }

  public abstract QuestType getType();

  public String getId() {
    return id;
  }

  public ItemStack getIcon() {
    return icon;
  }

  public int getGoal() {
    return goal;
  }

  public List<Reward> getRewards() {
    return rewards;
  }
}
