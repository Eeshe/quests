package me.eeshe.quests.menu.holders;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface MenuHolder extends InventoryHolder {

  void handleClick(InventoryClickEvent event);

  @Override
  default @NotNull Inventory getInventory() {
    return null;
  }
}
