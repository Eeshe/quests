package me.eeshe.quests.listeners;

import me.eeshe.quests.menu.holders.MenuHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuHandler implements Listener {

  @EventHandler
  public void onMenuClick(InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof MenuHolder menuHolder)) {
      return;
    }
    menuHolder.handleClick(event);
  }
}
