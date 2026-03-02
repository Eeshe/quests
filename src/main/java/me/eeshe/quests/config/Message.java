package me.eeshe.quests.config;

import me.eeshe.quests.config.registry.MessageRegistry;
import org.bukkit.command.CommandSender;

public class Message {
  private static MessageConfig MESSAGE_CONFIG;

  private String key;
  private boolean defaultEnabled;
  private String defaultValue;

  public Message(String key, boolean defaultEnabled, String defaultValue) {
    this.key = key;
    this.defaultEnabled = defaultEnabled;
    this.defaultValue = defaultValue;

    MessageRegistry.getInstance().add(this);
  }

  public static void setMessageConfig(MessageConfig messageConfig) {
    if (MESSAGE_CONFIG != null) {
      return;
    }
    MESSAGE_CONFIG = messageConfig;
  }

  public void send(CommandSender sender) {
    if (!MESSAGE_CONFIG.isEnabled(key, defaultEnabled)) {
      return;
    }
    sender.sendMessage(MESSAGE_CONFIG.getValue(key, defaultValue));
  }

  public String getKey() {
    return key;
  }

  public boolean getDefaultEnabled() {
    return defaultEnabled;
  }

  public String getDefaultValue() {
    return defaultValue;
  }
}
