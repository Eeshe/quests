package me.eeshe.quests.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import me.eeshe.quests.Quests;
import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.util.LogUtil;
import org.bukkit.plugin.Plugin;

public class MongoDatabase implements Database {
  private final Plugin plugin;
  private final PluginConfig pluginConfig;

  private MongoClient mongoClient;

  public MongoDatabase(Plugin plugin) {
    this.plugin = plugin;
    this.pluginConfig = ((Quests) plugin).getPluginConfig();
  }

  @Override
  public void connect() {
    final DatabaseSettings databaseSettings = pluginConfig.getDatabaseSettings();
    final String connectionString = generateConnectionString(databaseSettings);
    if (connectionString == null) {
      return;
    }
    this.mongoClient =
        MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build());
  }

  private String generateConnectionString(DatabaseSettings databaseSettings) {
    final String host = databaseSettings.host();
    if (host == null || host.isEmpty()) {
      LogUtil.warning("MongoDB host cannot be null or empty");
      return null;
    }
    final String user = databaseSettings.user();
    if (user == null || user.isEmpty()) {
      LogUtil.warning("MongoDB user cannot be null or empty");
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
      uri = String.format("mongodb://%s:%s@%s:%d/%s", user, password, host, port, database);
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
    mongoClient.close();
  }

  @Override
  public Plugin getPlugin() {
    return plugin;
  }
}
