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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.eeshe.quests.Quests;
import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.model.questplayer.QuestPlayer;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.repository.QuestRepository;
import me.eeshe.quests.util.LogUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public class MongoDatabase implements Database {
  private final Plugin plugin;
  private final PluginConfig pluginConfig;
  private final QuestRepository questRepository;
  private final QuestPlayerRepository questPlayerRepository;

  private String database;
  private MongoClient mongoClient;

  public MongoDatabase(final Quests plugin) {
    this.plugin = plugin;
    this.pluginConfig = plugin.getPluginConfig();
    this.questRepository = plugin.getQuestRepository();
    this.questPlayerRepository = plugin.getQuestPlayerRepository();
  }

  @Override
  public void connect() {
    final DatabaseSettings databaseSettings = pluginConfig.getDatabaseSettings();
    final String connectionString = generateConnectionString(databaseSettings);
    if (connectionString == null) {
      return;
    }
    this.database = databaseSettings.database();
    this.mongoClient =
        MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build());
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
      return new QuestPlayer(player, questRepository, questPlayerRepository);
    }
    final Set<String> completedQuests =
        new HashSet<>(document.getList("completedQuests", String.class, new ArrayList<>()));
    final String currentQuestId = document.getString("currentQuestId");
    final int currentQuestProgress = document.getInteger("currentQuestProgress", 0);

    return new QuestPlayer(
        player,
        completedQuests,
        currentQuestId,
        currentQuestProgress,
        questRepository,
        questPlayerRepository);
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
    System.out.println(1);
    if (mongoClient == null || questPlayer == null) {
      return;
    }
    System.out.println(2);
    final UUID playerUUid = questPlayer.getUuid();
    final MongoCollection<Document> collection =
        mongoClient.getDatabase(database).getCollection("quest_players");
    System.out.println(3);
    final Document document =
        new Document("_id", playerUUid.toString())
            .append("completedQuests", questPlayer.getCompletedQuestIds())
            .append("currentQuestId", questPlayer.getCurrentQuestId())
            .append("currentQuestProgress", questPlayer.getCurrentQuestProgress());
    System.out.println(4);

    final ReplaceOptions options = new ReplaceOptions().upsert(true);
    collection.replaceOne(eq("_id", playerUUid.toString()), document, options);
    System.out.println(5);
  }
}
