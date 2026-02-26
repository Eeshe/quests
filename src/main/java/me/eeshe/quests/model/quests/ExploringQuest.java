package me.eeshe.quests.model.quests;

import java.util.List;
import me.eeshe.quests.model.rewards.CommandsReward;
import me.eeshe.quests.model.rewards.ItemReward;
import me.eeshe.quests.model.rewards.Reward;
import me.eeshe.quests.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

public class ExploringQuest extends Quest implements TargetQuest {
  private final Biome target;

  public ExploringQuest(String id, ItemStack icon, int goal, List<Reward> rewards, Biome target) {
    super(id, icon, goal, rewards);
    this.target = target;
  }

  public static ExploringQuest createDefault() {
    final String id = "exploring";
    final ItemStack icon =
        ItemBuilder.builder().material(Material.COMPASS).displayName("&7Find a %target%").build();
    final int goal = 1;
    final ItemStack itemReward =
        ItemBuilder.builder().material(Material.GOLDEN_CARROT).amount(128).build();
    final List<Reward> rewards =
        List.of(
            new CommandsReward(
                List.of("eco give %player% 100", "say %player% completed quest %id%")),
            new ItemReward(itemReward));
    final Biome target = Biome.DESERT;

    return new ExploringQuest(id, icon, goal, rewards, target);
  }

  @Override
  public QuestType getType() {
    return QuestType.EXPLORING;
  }

  public Biome getTarget() {
    return target;
  }

  @Override
  public String getTargetString() {
    return target.getKey().toString();
  }
}
