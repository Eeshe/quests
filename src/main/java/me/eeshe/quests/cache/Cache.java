package me.eeshe.quests.cache;

import java.util.List;

public interface Cache<K, V> {

  V get(K key);

  void put(K key, V value);

  void remove(K key);

  void clear();

  List<V> getValues();
}
