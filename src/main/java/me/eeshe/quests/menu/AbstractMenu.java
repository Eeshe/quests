package me.eeshe.quests.menu;

import java.util.List;
import java.util.function.Function;
import me.eeshe.quests.config.Menu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractMenu {
  private static final int PAGE_SIZE = 28;

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
    final int menuSize = getMenu().getSize();
    for (int i = 0; i < menuSize; i++) {
      if (isFrameSlot(i, menuSize)) {
        inventory.setItem(i, frameItem);
      }
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
    int startIndex = computeStartIndex(page);
    int endIndex = computeEndIndex(page, items.size());

    int slotIndex = 0;
    for (int i = startIndex; i < endIndex; i++) {
      while (isFrameSlot(slotIndex, inventory.getSize())) {
        slotIndex++;
      }
      inventory.setItem(slotIndex, itemStackFunction.apply(items.get(i)));
      slotIndex++;
    }
    if (page > 1) {
      inventory.setItem(inventory.getSize() - 9, menu.getPreviousPageItem());
    }
    if (endIndex < items.size()) {
      inventory.setItem(inventory.getSize() - 1, menu.getNextPageItem());
    }
  }

  public boolean isFrameSlot(int slot, int totalSize) {
    int rows = totalSize / 9;
    int row = slot / 9;
    int col = slot % 9;
    return row == 0 || row == rows - 1 || col == 0 || col == 8;
  }

  public int computeEndIndex(int page, int itemAmount) {
    return Math.min(computeStartIndex(page) + PAGE_SIZE, itemAmount);
  }

  public int computeStartIndex(int page) {
    return (page - 1) * PAGE_SIZE;
  }

  public Menu getMenu() {
    return menu;
  }
}
