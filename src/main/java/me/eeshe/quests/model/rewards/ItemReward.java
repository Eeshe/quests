package me.eeshe.quests.model.rewards;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemReward implements Reward {
  private final ItemStack item;

  public ItemReward(ItemStack item) {
    this.item = item;
  }

  @Override
  public void apply(Player player) {
    if (item == null) {
      return;
    }
    final PlayerInventory playerInventory = player.getInventory();
    if (playerInventory.firstEmpty() != -1) {
      playerInventory.addItem(item);
      return;
    }
    player.getWorld().dropItem(player.getLocation(), item).setOwner(player.getUniqueId());
  }

  public ItemStack getItem() {
    return item;
  }
}
