package me.eeshe.quests.repository;

public interface Repository {

  void load();

  void unload();

  default void reload() {
    unload();
    load();
  }
}
