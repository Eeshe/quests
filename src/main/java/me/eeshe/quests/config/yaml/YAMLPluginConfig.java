package me.eeshe.quests.config.yaml;

import me.eeshe.quests.config.PluginConfig;
import me.eeshe.quests.database.DatabaseSettings;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class YAMLPluginConfig extends YAMLConfig implements PluginConfig {
  private static final String DATABASE_SETTINGS_KEY = "database-settings";
  private static final String DATABASE_SETTINGS_HOST_KEY = DATABASE_SETTINGS_KEY + ".host";
  private static final String DATABASE_SETTINGS_PORT_KEY = DATABASE_SETTINGS_KEY + ".port";
  private static final String DATABASE_SETTINGS_USER_KEY = DATABASE_SETTINGS_KEY + ".user";
  private static final String DATABASE_SETTINGS_PASSWORD_KEY = DATABASE_SETTINGS_KEY + ".password";
  private static final String DATABASE_SETTINGS_DATABASE_KEY = DATABASE_SETTINGS_KEY + ".database";

  public YAMLPluginConfig(Plugin plugin) {
    super(plugin, null, "config.yml");
  }

  @Override
  public void writeDefaults() {
    writeDefaultDatabaseSettings();
    super.writeDefaults();
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
  public DatabaseSettings getDatabaseSettings() {
    final FileConfiguration config = getConfig();
    final String host = config.getString(DATABASE_SETTINGS_HOST_KEY);
    final int port = config.getInt(DATABASE_SETTINGS_PORT_KEY);
    final String user = config.getString(DATABASE_SETTINGS_USER_KEY);
    final String password = config.getString(DATABASE_SETTINGS_PASSWORD_KEY);
    final String database = config.getString(DATABASE_SETTINGS_DATABASE_KEY);

    return new DatabaseSettings(host, port, user, password, database);
  }
}
