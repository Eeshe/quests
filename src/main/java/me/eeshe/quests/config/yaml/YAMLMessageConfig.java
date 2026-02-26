package me.eeshe.quests.config.yaml;

import me.eeshe.quests.config.MessageConfig;
import me.eeshe.quests.model.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class YAMLMessageConfig extends YAMLConfig implements MessageConfig {

  public YAMLMessageConfig(Plugin plugin) {
    super(plugin, null, "messages.yml");
  }

  @Override
  public void writeDefaults() {
    final FileConfiguration config = getConfig();
    for (Message message : Message.values()) {
      config.addDefault(message.getKey() + ".enabled", message.getDefaultEnabled());
      config.addDefault(message.getKey() + ".value", message.getDefaultValue());
    }
    super.writeDefaults();
  }

  @Override
  public boolean isEnabled(String key, boolean defaultValue) {
    return getConfig().getBoolean(key + ".enabled", defaultValue);
  }

  @Override
  public String getValue(String key, String defaultValue) {
    return getConfig().getString(key + ".value", defaultValue);
  }
}
