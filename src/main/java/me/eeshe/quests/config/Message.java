package me.eeshe.quests.config;

import java.util.HashMap;
import java.util.Map;
import me.eeshe.quests.config.registry.MessageRegistry;
import me.eeshe.quests.config.registry.SoundRegistry;
import me.eeshe.quests.util.PlaceholderUtil;
import me.eeshe.quests.util.StringUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

  public Message(String key, String defaultValue) {
    this.key = key;
    this.defaultEnabled = true;
    this.defaultValue = defaultValue;

    MessageRegistry.getInstance().add(this);
  }

  public static void setMessageConfig(MessageConfig messageConfig) {
    MESSAGE_CONFIG = messageConfig;
  }

  public void sendSuccess(CommandSender sender) {
    send(sender, SoundRegistry.SUCCESS);
  }

  public void sendSuccess(CommandSender sender, Map<String, String> placeholders) {
    send(sender, SoundRegistry.SUCCESS, placeholders);
  }

  public void sendError(CommandSender sender) {
    send(sender, SoundRegistry.ERROR);
  }

  public void sendError(CommandSender sender, Map<String, String> placeholders) {
    send(sender, SoundRegistry.ERROR, placeholders);
  }

  public void send(CommandSender sender, Sound libSound) {
    send(sender, false, libSound, new HashMap<>());
  }

  public void send(CommandSender sender, Map<String, String> placeholders) {
    send(sender, false, null, placeholders);
  }

  public void send(CommandSender sender, Sound libSound, Map<String, String> placeholders) {
    send(sender, false, libSound, placeholders);
  }

  public void send(
      CommandSender sender, boolean actionBar, Sound libSound, Map<String, String> placeholders) {
    String message = StringUtil.formatColor(getFormattedValue(placeholders));
    if (!actionBar) {
      sender.sendMessage(message);
    } else if (sender instanceof Player player) {
      player
          .spigot()
          .sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
    }
    if (libSound == null) return;

    libSound.play(sender);
  }

  public String getFormattedValue(Map<String, String> placeholders) {
    return PlaceholderUtil.formatPlaceholders(getValue(), placeholders);
  }

  public String getValue() {
    return MESSAGE_CONFIG.getValue(key, defaultValue);
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
