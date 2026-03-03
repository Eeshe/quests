package me.eeshe.quests.menu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
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
import me.eeshe.quests.util.LogUtil;
import me.eeshe.quests.util.PlaceholderUtil;
import me.eeshe.quests.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;
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
    final List<Quest> quests = questRepository.getAll();
    final IQuestPlayer questPlayer = questPlayerRepository.get(player);
    quests.sort(
        Comparator.comparing(Quest::getId, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(quest -> questPlayer.hasCompletedQuest(quest)) // completed last
        );

    setFrameItems(inventory);
    addPageItems(inventory, quests, this::createQuestItem, page);
    fillEmpty(inventory);

    return inventory;
  }

  private ItemStack createQuestItem(Quest quest) {
    final ItemStack item = quest.getIcon();
    final Map<String, Object> additionalConfigurations = getMenu().getAdditionalConfigurations();

    final IQuestPlayer questPlayer = questPlayerRepository.get(player);
    Object loreObj = additionalConfigurations.get("quest-lore");
    List<String> questLore;
    if (loreObj instanceof List) {
      questLore = (List<String>) loreObj;
    } else {
      LogUtil.warning(
          "QuestListMenu.createQuestItem: 'quest-lore' expected List<String> but got "
              + (loreObj == null ? "null" : loreObj.getClass().getName()));
      questLore = new ArrayList<>();
    }
    Object statusMapObj = additionalConfigurations.get("quest-status");
    ConfigurationSection questStatusSection;
    if (statusMapObj instanceof ConfigurationSection) {
      questStatusSection = (ConfigurationSection) statusMapObj;
    } else {
      LogUtil.warning(
          "QuestListMenu.createQuestItem: 'quest-status' expected Map<String,Object> but got "
              + (statusMapObj == null ? "null" : statusMapObj.getClass().getName()));
      questStatusSection = null;
    }
    final String questStatusKey =
        questPlayer.hasCompletedQuest(quest) ? "completed" : "in-progress";
    String questStatus = questStatusSection.getString(questStatusKey);

    final DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
    final Map<String, String> placeholders =
        new HashMap<>(
            Map.ofEntries(
                Map.entry("%progress%", decimalFormat.format(questPlayer.getQuestProgress(quest))),
                Map.entry("%required%", decimalFormat.format(quest.getGoal())),
                Map.entry(
                    "%percentage%",
                    String.valueOf(
                        decimalFormat.format(
                            ((double) questPlayer.getQuestProgress(quest)
                                    / (double) quest.getGoal())
                                * 100))),
                Map.entry("%status%", questStatus)));
    if (quest instanceof TargetQuest targetQuest) {
      placeholders.put("%target%", StringUtil.formatEnum(targetQuest.getTargetString()));
    }

    return PlaceholderUtil.formatPlaceholders(
        player,
        new ItemBuilder(item).addLore(questLore).addLore(questStatus).build(),
        placeholders);
  }
}
