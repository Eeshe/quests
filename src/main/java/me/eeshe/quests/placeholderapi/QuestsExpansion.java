package me.eeshe.quests.placeholderapi;

import java.text.DecimalFormat;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.eeshe.quests.Quests;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.repository.QuestRepository;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestsExpansion extends PlaceholderExpansion {
  private final Quests plugin;
  private final QuestRepository questRepository;
  private final QuestPlayerRepository questPlayerRepository;

  public QuestsExpansion(Quests plugin) {
    this.plugin = plugin;
    this.questRepository = plugin.getQuestRepository();
    this.questPlayerRepository = plugin.getQuestPlayerRepository();
  }

  @Override
  public @NotNull String getIdentifier() {
    return "quests";
  }

  @Override
  public @NotNull String getAuthor() {
    return String.join(", ", plugin.getPluginMeta().getAuthors());
  }

  @Override
  public @NotNull String getVersion() {
    return plugin.getPluginMeta().getVersion();
  }

  @Override
  public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
    return computePlaceholder(player, params);
  }

  @Override
  public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
    return computePlaceholder(player, params);
  }

  private String computePlaceholder(OfflinePlayer player, String params) {
    final IQuestPlayer questPlayer = questPlayerRepository.get(player);
    if (questPlayer == null) {
      return "N/A";
    }
    if (params.equals("completed_total")) {
      return String.valueOf(questPlayer.getCompletedQuestIds().size());
    }
    if (!params.startsWith("progress_")) {
      return null;
    }
    final String[] splitParams = params.split("_");
    if (splitParams.length < 2) {
      return null;
    }
    final String questId = splitParams[1];
    final Quest quest = questRepository.get(questId);
    if (quest == null) {
      return "N/A";
    }
    return new DecimalFormat("#,###.##").format(questPlayer.getQuestProgress(quest));
  }
}
