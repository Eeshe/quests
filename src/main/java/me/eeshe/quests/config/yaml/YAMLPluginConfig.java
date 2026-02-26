package me.eeshe.quests.config.yaml;

import java.util.ArrayList;
import java.util.List;
import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.database.DatabaseSettings;
import me.eeshe.quests.model.quests.ExploringQuest;
import me.eeshe.quests.model.quests.KillingQuest;
import me.eeshe.quests.model.quests.MiningQuest;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.model.quests.QuestType;
import me.eeshe.quests.model.quests.RunningQuest;
import me.eeshe.quests.model.quests.TargetQuest;
import me.eeshe.quests.model.rewards.CommandsReward;
import me.eeshe.quests.model.rewards.ItemReward;
import me.eeshe.quests.model.rewards.Reward;
import me.eeshe.quests.util.LogUtil;
import me.eeshe.quests.util.YAMLUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class YAMLPluginConfig extends YAMLConfig implements PluginConfig {
  private static final String DATABASE_SETTINGS_KEY = "database-settings";
  private static final String DATABASE_SETTINGS_HOST_KEY = DATABASE_SETTINGS_KEY + ".host";
  private static final String DATABASE_SETTINGS_PORT_KEY = DATABASE_SETTINGS_KEY + ".port";
  private static final String DATABASE_SETTINGS_USER_KEY = DATABASE_SETTINGS_KEY + ".user";
  private static final String DATABASE_SETTINGS_PASSWORD_KEY = DATABASE_SETTINGS_KEY + ".password";
  private static final String DATABASE_SETTINGS_DATABASE_KEY = DATABASE_SETTINGS_KEY + ".database";

  private static final String QUESTS_KEY = "quests";

  public YAMLPluginConfig(Plugin plugin) {
    super(plugin, null, "config.yml");
  }

  @Override
  public void writeDefaults() {
    PluginConfig.super.writeDefaults();
  }

  @Override
  public void writeDefaultDatabaseSettings() {
    final FileConfiguration config = getConfig();

    config.addDefault(DATABASE_SETTINGS_HOST_KEY, "localhost");
    config.addDefault(DATABASE_SETTINGS_PORT_KEY, 27017);
    config.addDefault(DATABASE_SETTINGS_USER_KEY, "user");
    config.addDefault(DATABASE_SETTINGS_PASSWORD_KEY, "password");
    config.addDefault(DATABASE_SETTINGS_DATABASE_KEY, "quests");
  }

  @Override
  public void writeDefaultQuests() {
    if (getConfig().contains(QUESTS_KEY)) {
      // Check if it exists due to YAMLUtil methods using .set instead of .addDefault
      return;
    }
    final List<Quest> defaultQuests =
        List.of(
            MiningQuest.createDefault(),
            KillingQuest.createDefault(),
            RunningQuest.createDefault(),
            ExploringQuest.createDefault());
    for (Quest defaultQuest : defaultQuests) {
      writeQuest(defaultQuest);
    }
  }

  private void writeQuest(Quest quest) {
    final FileConfiguration config = getConfig();
    final String key = QUESTS_KEY + "." + quest.getId();

    config.addDefault(key + ".type", quest.getType().name());
    YAMLUtil.writeItemStack(config, key + ".icon", quest.getIcon());
    config.addDefault(key + ".goal", quest.getGoal());
    writeQuestRewards(quest);
    if (quest instanceof TargetQuest targetQuest) {
      config.addDefault(key + ".target", targetQuest.getTargetString());
    }
  }

  private void writeQuestRewards(Quest quest) {
    final String key = QUESTS_KEY + "." + quest.getId() + ".rewards";

    writeCommandRewards(key + ".commands", quest);
    writeItemRewards(key + ".items", quest);
  }

  private void writeCommandRewards(String key, Quest quest) {
    final List<CommandsReward> commandRewards =
        quest.getRewards().stream()
            .filter(reward -> reward instanceof CommandsReward)
            .map(CommandsReward.class::cast)
            .toList();

    final FileConfiguration config = getConfig();
    int index = 1;
    for (CommandsReward commandReward : commandRewards) {
      config.addDefault(key + "." + index + ".commands", commandReward.getCommands());
    }
  }

  private void writeItemRewards(String key, Quest quest) {
    final List<ItemReward> itemRewards =
        quest.getRewards().stream()
            .filter(reward -> reward instanceof ItemReward)
            .map(ItemReward.class::cast)
            .toList();

    final FileConfiguration config = getConfig();
    int index = 1;
    for (ItemReward itemReward : itemRewards) {
      YAMLUtil.writeItemStack(config, key + "." + index + ".item", itemReward.getItem());
    }
  }

  @Override
  public DatabaseSettings getDatabaseSettings() {
    final FileConfiguration config = getConfig();
    final String host = config.getString(DATABASE_SETTINGS_HOST_KEY);
    final int port = config.getInt(DATABASE_SETTINGS_PORT_KEY);
    final String user = config.getString(DATABASE_SETTINGS_USER_KEY);
    final String password = config.getString(DATABASE_SETTINGS_PASSWORD_KEY);
    final String database = config.getString(DATABASE_SETTINGS_DATABASE_KEY);

    return new DatabaseSettings(host, port, user, password, database);
  }

  @Override
  public List<Quest> getQuests() {
    final List<Quest> quests = new ArrayList<>();
    final ConfigurationSection questSection = getConfig().getConfigurationSection(QUESTS_KEY);
    for (String questId : questSection.getKeys(false)) {
      final Quest quest = getQuest(questId);
      if (quest == null) {
        continue;
      }
      quests.add(quest);
    }
    return quests;
  }

  private Quest getQuest(String questId) {
    final FileConfiguration config = getConfig();
    final String key = QUESTS_KEY + "." + questId;
    final ConfigurationSection questSection = config.getConfigurationSection(key);

    final QuestType questType = QuestType.fromName(questSection.getString("type"));
    if (questType == null) {
      LogUtil.warning("Unknown quest type '" + questType + "' configured for '" + questId + "'.");
      return null;
    }
    final ItemStack icon = YAMLUtil.readItemStack(config, key + ".icon");
    final int goal = questSection.getInt("goal");
    final List<Reward> rewards = getQuestRewards(key + ".rewards");
    final String targetName = questSection.getString("target");

    return switch (questType) {
      case QuestType.RUNNING -> {
        yield new RunningQuest(questId, icon, goal, rewards);
      }

      case QuestType.MINING -> {
        final Material target = Material.matchMaterial(targetName);
        if (target == null) {
          LogUtil.warning(
              "Unknown mining target '" + targetName + "' configured for '" + questId + "'.");
          yield null;
        }
        yield new MiningQuest(questId, icon, goal, rewards, target);
      }

      case QuestType.KILLING -> {
        final EntityType target = EntityType.fromName(targetName);
        if (target == null) {
          LogUtil.warning(
              "Unknown killing target '" + targetName + "' configured for '" + questId + "'.");
          yield null;
        }
        yield new KillingQuest(questId, icon, goal, rewards, target);
      }

      case QuestType.EXPLORING -> {
        final Biome target = Registry.BIOME.get(NamespacedKey.fromString(targetName));
        if (target == null) {
          LogUtil.warning(
              "Unknown exploring target '" + targetName + "' configured for '" + questId + "'.");
          yield null;
        }
        yield new ExploringQuest(questId, icon, goal, rewards, target);
      }
    };
  }

  private List<Reward> getQuestRewards(String key) {
    final List<Reward> rewards = new ArrayList<>();
    final ConfigurationSection rewardSection = getConfig().getConfigurationSection(key);
    if (rewardSection == null) {
      return rewards;
    }
    for (String rewardType : rewardSection.getKeys(false)) {
      final String rewardKey = rewardSection.getCurrentPath() + "." + rewardType;
      switch (rewardType) {
        case "commands" -> {
          rewards.addAll(getCommandsRewards(rewardKey));
        }
        case "items" -> {
          rewards.addAll(getItemRewards(rewardKey));
        }
      }
    }
    LogUtil.warning("REWARDS: " + rewards.size());
    return rewards;
  }

  private List<CommandsReward> getCommandsRewards(String key) {
    final List<CommandsReward> commandsRewards = new ArrayList<>();
    final ConfigurationSection rewardSection = getConfig().getConfigurationSection(key);
    if (rewardSection == null) {
      return commandsRewards;
    }
    for (String index : rewardSection.getKeys(false)) {
      final List<String> commands = rewardSection.getStringList(index + ".commands");

      commandsRewards.add(new CommandsReward(commands));
    }
    LogUtil.warning("COMMAND REWARDS: " + commandsRewards.size());
    return commandsRewards;
  }

  private List<ItemReward> getItemRewards(String key) {
    final List<ItemReward> itemRewards = new ArrayList<>();
    final ConfigurationSection rewardSection = getConfig().getConfigurationSection(key);
    if (rewardSection == null) {
      return itemRewards;
    }
    for (String index : rewardSection.getKeys(false)) {
      final ItemStack item =
          YAMLUtil.readItemStack(
              getConfig(), rewardSection.getCurrentPath() + "." + index + ".item");
      if (item == null) {
        continue;
      }

      itemRewards.add(new ItemReward(item));
    }
    LogUtil.warning("ITEM REWARDS: " + itemRewards.size());
    return itemRewards;
  }
}
