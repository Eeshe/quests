package me.eeshe.quests.menu;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.eeshe.quests.Quests;
import me.eeshe.quests.config.registry.MenuRegistry;
import me.eeshe.quests.menu.holders.QuestListMenuHolder;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.model.quests.TargetQuest;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.repository.QuestRepository;
import me.eeshe.quests.util.ItemBuilder;
import me.eeshe.quests.util.PlaceholderUtil;
import me.eeshe.quests.util.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QuestListMenu extends PagedMenu {
  private final Player player;
  private final int page;
  private final QuestRepository questRepository;
  private final QuestPlayerRepository questPlayerRepository;

  public QuestListMenu(Player player, int page, Quests plugin) {
    super(MenuRegistry.QUEST_LIST);
    this.player = player;
    this.page = page;
    this.questRepository = plugin.getQuestRepository();
    this.questPlayerRepository = plugin.getQuestPlayerRepository();
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
    final Map<String, Object> additionalConfigurations = getMenu().getAdditionalConfigurations();

    final List<String> questLore = (List<String>) additionalConfigurations.get("quest-lore");
    final IQuestPlayer questPlayer = questPlayerRepository.get(player);
    final String questStatusKey =
        questPlayer.hasCompletedQuest(quest) ? "completed" : "in-progress";
    final Map<String, Object> questStatusMap =
        (Map<String, Object>) additionalConfigurations.get("quest-status");
    final String questStatus = (String) questStatusMap.get(questStatusKey);

    final Map<String, String> placeholders =
        new HashMap<>(
            Map.ofEntries(
                Map.entry("%progress%", String.valueOf(questPlayer.getCurrentQuestProgress())),
                Map.entry("%required%", String.valueOf(quest.getGoal())),
                Map.entry(
                    "%percentage%",
                    String.valueOf(
                        new DecimalFormat("#.##")
                            .format(
                                ((double) questPlayer.getCurrentQuestProgress()
                                        / (double) quest.getGoal())
                                    * 100))),
                Map.entry("%status%", questStatus)));
    if (quest instanceof TargetQuest targetQuest) {
      placeholders.put("%target%", StringUtil.formatEnum(targetQuest.getTargetString()));
    }

    return PlaceholderUtil.formatPlaceholders(
        new ItemBuilder(item).addLore(questLore).addLore(questStatus).build(), placeholders);
  }
}
