package me.eeshe.quests.model.quests;

import java.util.List;
import me.eeshe.quests.model.rewards.CommandsReward;
import me.eeshe.quests.model.rewards.ItemReward;
import me.eeshe.quests.model.rewards.Reward;
import me.eeshe.quests.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class RunningQuest extends Quest {

  public RunningQuest(String id, ItemStack icon, int goal, List<Reward> rewards) {
    super(id, icon, goal, rewards);
  }

  public static RunningQuest createDefault() {
    final String id = "running";
    final ItemStack icon =
        ItemBuilder.builder()
            .material(Material.LEATHER_BOOTS)
            .displayName("&7Run %required% blocks")
            .addFlag(ItemFlag.HIDE_ATTRIBUTES)
            .build();
    final int goal = 200;
    final ItemStack itemReward =
        ItemBuilder.builder()
            .material(Material.LEATHER_BOOTS)
            .addAttributeModifier(
                Attribute.MOVEMENT_SPEED,
                new AttributeModifier(
                    NamespacedKey.minecraft("speed"),
                    0.1,
                    Operation.ADD_NUMBER,
                    EquipmentSlotGroup.FEET))
            .build();
    final List<Reward> rewards =
        List.of(
            new CommandsReward(
                List.of("eco give %player% 100", "say %player% completed a running quest")),
            new ItemReward(itemReward));

    return new RunningQuest(id, icon, goal, rewards);
  }

  @Override
  public QuestType getType() {
    return QuestType.RUNNING;
  }
}
