package me.eeshe.quests.config;

public interface Config {

  void load();

  void writeDefaults();

  void save();
}
