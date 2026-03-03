package me.eeshe.quests.model.quests;

import java.util.List;
import me.eeshe.quests.model.rewards.CommandsReward;
import me.eeshe.quests.model.rewards.ItemReward;
import me.eeshe.quests.model.rewards.Reward;
import me.eeshe.quests.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KillingQuest extends Quest implements TargetQuest {
  private final EntityType target;

  public KillingQuest(
      String id, ItemStack icon, int goal, List<Reward> rewards, EntityType target) {
    super(id, icon, goal, rewards);
    this.target = target;
  }

  public static KillingQuest createDefault() {
    final String id = "killing";
    final ItemStack icon =
        ItemBuilder.builder()
            .material(Material.ZOMBIE_HEAD)
            .displayName("&7Kill %required% %target%")
            .build();
    final int goal = 50;
    final ItemStack itemReward =
        ItemBuilder.builder()
            .material(Material.IRON_SWORD)
            .addEnchantment(Enchantment.SMITE, 3)
            .build();
    final List<Reward> rewards =
        List.of(
            new CommandsReward(
                List.of("eco give %player% 100", "say %player% completed quest %id%")),
            new ItemReward(itemReward));
    final EntityType target = EntityType.ZOMBIE;

    return new KillingQuest(id, icon, goal, rewards, target);
  }

  @Override
  public QuestType getType() {
    return QuestType.KILLING;
  }

  public EntityType getTarget() {
    return target;
  }

  @Override
  public String getTargetString() {
    return target.name();
  }
}
