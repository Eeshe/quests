package me.eeshe.quests.config.registry;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigRegistry<T> {
  private final List<T> REGISTRY = new ArrayList<>();

  public void add(T t) {
    REGISTRY.add(t);
  }

  public List<T> values() {
    return REGISTRY;
  }
}
