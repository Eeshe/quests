package me.eeshe.quests.menu.holders;

import me.eeshe.quests.Quests;
import me.eeshe.quests.config.registry.MenuRegistry;
import me.eeshe.quests.config.registry.SoundRegistry;
import me.eeshe.quests.menu.QuestListMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class QuestListMenuHolder implements PagedMenuHolder {
  private final int page;

  public QuestListMenuHolder(int page) {
    this.page = page;
  }

  @Override
  public void handleClick(Quests plugin, InventoryClickEvent event) {
    event.setCancelled(true);
    final ItemStack clickedItem = event.getCurrentItem();
    if (clickedItem == null) {
      return;
    }
    if (clickedItem.isSimilar(MenuRegistry.QUEST_LIST.get().getFrameItem())) {
      return;
    }
    final int clickedSlot = event.getSlot() + 1;
    final Player player = (Player) event.getWhoClicked();
    if (clickedSlot == event.getInventory().getSize()) {
      player.openInventory(new QuestListMenu(player, page + 1, plugin).create());
    } else if (clickedSlot == event.getInventory().getSize() - 8) {
      player.openInventory(new QuestListMenu(player, page - 1, plugin).create());
    } else {
      return;
    }
    SoundRegistry.PAGE_CHANGE.play(player);
  }

  @Override
  public int getPage() {
    return page;
  }
}
