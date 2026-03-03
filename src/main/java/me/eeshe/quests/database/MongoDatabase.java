package me.eeshe.quests.database;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import me.eeshe.quests.Quests;
import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.model.questplayer.QuestPlayer;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.util.LogUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class MongoDatabase implements Database {
  private final Plugin plugin;
  private final PluginConfig pluginConfig;

  private String database;
  private MongoClient mongoClient;
  private QuestPlayerRepository questPlayerRepository;

  public MongoDatabase(final Quests plugin) {
    this.plugin = plugin;
    this.pluginConfig = plugin.getPluginConfig();
  }

  @Override
  public CompletableFuture<Void> connect() {
    final CompletableFuture<Void> future = new CompletableFuture<>();
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              final DatabaseSettings databaseSettings = pluginConfig.getDatabaseSettings();
              final String connectionString = generateConnectionString(databaseSettings);
              if (connectionString == null) {
                future.complete(null);
              }
              LogUtil.info("Connecting to MongoDB database...");
              this.database = databaseSettings.database();
              this.mongoClient =
                  MongoClients.create(
                      MongoClientSettings.builder()
                          .applyConnectionString(new ConnectionString(connectionString))
                          .build());
              LogUtil.info("Successfully connected to MongoDB database.");
              future.complete(null);
            });
    return future;
  }

  private String generateConnectionString(final DatabaseSettings databaseSettings) {
    final String host = databaseSettings.host();
    if (host == null || host.isEmpty()) {
      LogUtil.warning("MongoDB host cannot be null or empty");
      return null;
    }
    String user = databaseSettings.user();
    if (user == null) {
      LogUtil.warning("MongoDB user cannot be null");
      return null;
    }
    final String password = databaseSettings.password();
    if (password == null) {
      LogUtil.warning("MongoDB password cannot be null");
      return null;
    }
    final String database = databaseSettings.database();
    if (database == null || database.isEmpty()) {
      LogUtil.warning("MongoDB database cannot be null or empty");
      return null;
    }
    final int port = databaseSettings.port();

    String uri;
    final boolean isLocal = host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1");
    if (isLocal) {
      // Include credentials only if they are provided.
      if ((user != null && !user.isEmpty()) && (password != null && !password.isEmpty())) {
        uri = String.format("mongodb://%s:%s@%s:%d/%s", user, password, host, port, database);
      } else {
        // No auth – just host/port/database
        uri = String.format("mongodb://%s:%d/%s", host, port, database);
      }
    } else {
      uri =
          String.format(
              "mongodb+srv://%s:%s@%s/%s?retryWrites=true&w=majority",
              user, password, host, database);
    }
    return uri;
  }

  @Override
  public boolean isConnected() {
    return mongoClient != null;
  }

  @Override
  public void setRepositories(QuestPlayerRepository questPlayerRepository) {
    this.questPlayerRepository = questPlayerRepository;
  }

  @Override
  public void disconnect() {
    if (mongoClient == null) {
      return;
    }
    mongoClient.close();
  }

  @Override
  public Plugin getPlugin() {
    return plugin;
  }

  @Override
  public CompletableFuture<IQuestPlayer> fetchQuestPlayerAsync(final OfflinePlayer player) {
    final CompletableFuture<IQuestPlayer> future = new CompletableFuture<>();
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try {
                future.complete(fetchQuestPlayer(player));
              } catch (final Exception e) {
                future.completeExceptionally(e);
              }
            });
    return future;
  }

  @Override
  public IQuestPlayer fetchQuestPlayer(final OfflinePlayer player) {
    if (mongoClient == null || player == null) {
      return null;
    }
    final UUID playerUuid = player.getUniqueId();
    final MongoCollection<Document> collection =
        mongoClient.getDatabase(database).getCollection("quest_players");
    final Document document = collection.find(eq("_id", playerUuid.toString())).first();
    if (document == null) {
      return new QuestPlayer(player, questPlayerRepository);
    }
    final Set<String> completedQuests =
        new HashSet<>(document.getList("completedQuests", String.class, new ArrayList<>()));
    final Map<String, Integer> map = document.get("questProgress", Map.class);
    final ConcurrentHashMap<String, Integer> questProgress;
    if (map == null) {
      questProgress = new ConcurrentHashMap<>();
    } else {
      questProgress = new ConcurrentHashMap<>(map);
    }
    return new QuestPlayer(player, completedQuests, questProgress, questPlayerRepository);
  }

  @Override
  public CompletableFuture<Void> saveQuestPlayerAsync(final IQuestPlayer questPlayer) {
    final CompletableFuture<Void> future = new CompletableFuture<>();
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try {
                saveQuestPlayer(questPlayer);
                future.complete(null);
              } catch (final Exception e) {
                future.completeExceptionally(e);
              }
            });
    return future;
  }

  @Override
  public void saveQuestPlayer(final IQuestPlayer questPlayer) {
    if (mongoClient == null || questPlayer == null) {
      return;
    }
    final UUID playerUUid = questPlayer.getUuid();
    final MongoCollection<Document> collection =
        mongoClient.getDatabase(database).getCollection("quest_players");
    final Document document =
        new Document("_id", playerUUid.toString())
            .append("completedQuests", questPlayer.getCompletedQuestIds())
            .append("questProgress", questPlayer.getQuestProgress());

    final ReplaceOptions options = new ReplaceOptions().upsert(true);
    collection.replaceOne(eq("_id", playerUUid.toString()), document, options);
  }
}
