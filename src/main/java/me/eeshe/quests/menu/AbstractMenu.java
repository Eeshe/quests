package me.eeshe.quests.menu;

import java.util.List;
import java.util.function.Function;
import me.eeshe.quests.config.Menu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractMenu {
  private final Menu menu;

  public AbstractMenu(Menu menu) {
    this.menu = menu;
  }

  public abstract Inventory create();

  public void setFrameItems(Inventory inventory) {
    final ItemStack frameItem = menu.getFrameItem();
    if (frameItem == null) {
      return;
    }
    // TODO: Handle dynamic frame slots (-1)
    for (Integer frameSlot : menu.getFrameSlots()) {
      inventory.setItem(frameSlot, frameItem);
    }
  }

  public void fillEmpty(Inventory inventory) {
    final ItemStack fillerItem = menu.getFillerItem();
    if (fillerItem == null) {
      return;
    }
    for (int slot = 0; slot < inventory.getSize(); slot++) {
      if (inventory.getItem(slot) != null) {
        continue;
      }
      inventory.setItem(slot, fillerItem);
    }
  }

  public <T> void addPageItems(
      Inventory inventory, List<T> items, Function<T, ItemStack> itemStackFunction, int page) {
    // TODO
  }

  public Menu getMenu() {
    return menu;
  }
}
