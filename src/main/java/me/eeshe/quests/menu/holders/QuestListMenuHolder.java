package me.eeshe.quests.menu.holders;

import org.bukkit.event.inventory.InventoryClickEvent;

public class QuestListMenuHolder implements PagedMenuHolder {
  private final int page;

  public QuestListMenuHolder(int page) {
    this.page = page;
  }

  @Override
  public void handleClick(InventoryClickEvent event) {}

  @Override
  public int getPage() {
    return page;
  }
}
