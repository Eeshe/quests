package me.eeshe.quests.config;

public interface MessageConfig extends Config {

  boolean isEnabled(String key, boolean defaultValue);

  String getValue(String key, String defaultValue);
}
