package me.eeshe.quests.model;

import java.util.ArrayList;
import java.util.List;
import me.eeshe.quests.config.MessageConfig;
import org.bukkit.command.CommandSender;

public class Message {
  private static final List<Message> MESSAGES = new ArrayList<>();
  private static MessageConfig MESSAGE_CONFIG;

  public static final Message TEST = new Message("test", true, "This is a test message");

  private String key;
  private boolean defaultEnabled;
  private String defaultValue;

  public Message(String key, boolean defaultEnabled, String defaultValue) {
    this.key = key;
    this.defaultEnabled = defaultEnabled;
    this.defaultValue = defaultValue;

    MESSAGES.add(this);
  }

  public static void setMessageConfig(MessageConfig messageConfig) {
    if (MESSAGE_CONFIG != null) {
      return;
    }
    MESSAGE_CONFIG = messageConfig;
  }

  public static List<Message> values() {
    return MESSAGES;
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
