package me.eeshe.quests.database;

import java.util.concurrent.CompletableFuture;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public interface Database {

  void connect();

  void disconnect();

  IQuestPlayer fetchQuestPlayer(OfflinePlayer player);

  CompletableFuture<IQuestPlayer> fetchQuestPlayerAsync(OfflinePlayer player);

  void saveQuestPlayer(IQuestPlayer questPlayer);

  CompletableFuture<Void> saveQuestPlayerAsync(IQuestPlayer questPlayer);

  Plugin getPlugin();
}
