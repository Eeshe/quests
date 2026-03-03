package me.eeshe.quests.database;

import java.util.concurrent.CompletableFuture;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.repository.QuestPlayerRepository;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public interface Database {

  CompletableFuture<Void> connect();

  void disconnect();

  boolean isConnected();

  void setRepositories(QuestPlayerRepository questPlayerRepository);

  IQuestPlayer fetchQuestPlayer(OfflinePlayer player);

  CompletableFuture<IQuestPlayer> fetchQuestPlayerAsync(OfflinePlayer player);

  void saveQuestPlayer(IQuestPlayer questPlayer);

  CompletableFuture<Void> saveQuestPlayerAsync(IQuestPlayer questPlayer);

  Plugin getPlugin();
}
