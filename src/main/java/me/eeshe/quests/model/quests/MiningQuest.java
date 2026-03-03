package me.eeshe.quests.model.quests;

import java.util.List;
import me.eeshe.quests.model.rewards.CommandsReward;
import me.eeshe.quests.model.rewards.ItemReward;
import me.eeshe.quests.model.rewards.Reward;
import me.eeshe.quests.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class MiningQuest extends Quest implements TargetQuest {
  private final Material target;

  public MiningQuest(String id, ItemStack icon, int goal, List<Reward> rewards, Material target) {
    super(id, icon, goal, rewards);
    this.target = target;
  }

  public static MiningQuest createDefault() {
    final String id = "mining";
    final ItemStack icon =
        ItemBuilder.builder()
            .material(Material.COAL_ORE)
            .displayName("&7Mine %required% %target%")
            .build();
    final int goal = 50;
    final ItemStack itemReward =
        ItemBuilder.builder()
            .material(Material.IRON_PICKAXE)
            .addEnchantment(Enchantment.EFFICIENCY, 3)
            .build();
    final List<Reward> rewards =
        List.of(
            new CommandsReward(
                List.of("eco give %player% 100", "say %player% completed a mining quest")),
            new ItemReward(itemReward));
    final Material target = Material.COAL_ORE;

    return new MiningQuest(id, icon, goal, rewards, target);
  }

  @Override
  public QuestType getType() {
    return QuestType.MINING;
  }

  public Material getTarget() {
    return target;
  }

  @Override
  public String getTargetString() {
    return target.name();
  }
}
