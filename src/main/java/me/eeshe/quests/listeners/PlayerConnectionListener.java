package me.eeshe.quests.listeners;

import me.eeshe.quests.repository.QuestPlayerRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
  private final QuestPlayerRepository questPlayerRepository;

  public PlayerConnectionListener(QuestPlayerRepository questPlayerRepository) {
    this.questPlayerRepository = questPlayerRepository;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    questPlayerRepository.put(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    questPlayerRepository.remove(event.getPlayer());
  }
}
