package me.eeshe.quests.cache;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapCache<K, V> implements Cache<K, V> {
  private final ConcurrentHashMap<K, V> cacheMap = new ConcurrentHashMap<>();

  @Override
  public V get(K key) {
    return cacheMap.get(key);
  }

  @Override
  public void put(K key, V value) {
    cacheMap.put(key, value);
  }

  @Override
  public void remove(K key) {
    cacheMap.remove(key);
  }

  @Override
  public void clear() {
    cacheMap.clear();
  }
}
