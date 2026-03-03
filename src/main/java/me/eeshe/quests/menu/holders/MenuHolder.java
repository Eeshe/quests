package me.eeshe.quests.menu.holders;

import me.eeshe.quests.Quests;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface MenuHolder extends InventoryHolder {

  void handleClick(Quests plugin, InventoryClickEvent event);

  @Override
  default @NotNull Inventory getInventory() {
    return null;
  }
}
