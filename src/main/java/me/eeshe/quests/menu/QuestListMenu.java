package me.eeshe.quests.menu;

import me.eeshe.quests.config.registry.MenuRegistry;
import me.eeshe.quests.menu.holders.QuestListMenuHolder;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.repository.QuestRepository;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QuestListMenu extends PagedMenu {
  private final Player player;
  private final int page;
  private final QuestRepository questRepository;

  public QuestListMenu(Player player, int page, QuestRepository questRepository) {
    super(MenuRegistry.QUEST_LIST);
    this.player = player;
    this.page = page;
    this.questRepository = questRepository;
  }

  @Override
  public Inventory create() {
    final Inventory inventory = getMenu().createInventory(new QuestListMenuHolder(page), player);

    setFrameItems(inventory);
    addPageItems(inventory, questRepository.getAll(), this::createQuestItem, page);
    fillEmpty(inventory);

    return inventory;
  }

  private ItemStack createQuestItem(Quest quest) {
    final ItemStack item = quest.getIcon();
    // TODO
    return null;
  }
}
