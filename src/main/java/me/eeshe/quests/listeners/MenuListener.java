package me.eeshe.quests.listeners;

import me.eeshe.quests.Quests;
import me.eeshe.quests.menu.holders.MenuHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
  private final Quests plugin;

  public MenuListener(Quests plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onMenuClick(InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof MenuHolder menuHolder)) {
      return;
    }
    menuHolder.handleClick(plugin, event);
  }
}
