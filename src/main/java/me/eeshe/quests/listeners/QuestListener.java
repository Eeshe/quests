package me.eeshe.quests.listeners;

import java.util.Set;
import me.eeshe.quests.Quests;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.model.quests.ExploringQuest;
import me.eeshe.quests.model.quests.KillingQuest;
import me.eeshe.quests.model.quests.MiningQuest;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.model.quests.RunningQuest;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.repository.QuestRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class QuestListener implements Listener {
  private final Quests plugin;
  private final QuestRepository questRepository;
  private final QuestPlayerRepository questPlayerRepository;

  public QuestListener(Quests plugin) {
    this.plugin = plugin;
    this.questRepository = plugin.getQuestRepository();
    this.questPlayerRepository = plugin.getQuestPlayerRepository();
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    if (event.isCancelled()) {
      return;
    }
    final Material blockType = event.getBlock().getType();
    runAsynchronously(
        () -> {
          final Set<MiningQuest> miningQuests = questRepository.getMiningQuests(blockType);
          increaseProgress(event.getPlayer(), miningQuests, 1);
        });
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!(event.getEntity() instanceof LivingEntity damaged)) {
      return;
    }
    if (damaged.getHealth() - event.getFinalDamage() > 0) {
      return;
    }
    final Player damagerPlayer;
    if (event.getDamager() instanceof Player player) {
      damagerPlayer = player;
    } else if (event.getDamager() instanceof Projectile projectile
        && projectile.getShooter() instanceof Player) {
      damagerPlayer = (Player) projectile.getShooter();
    } else {
      return;
    }
    final EntityType entityType = damaged.getType();
    runAsynchronously(
        () -> {
          final Set<KillingQuest> killingQuests = questRepository.getKillingQuests(entityType);
          increaseProgress(damagerPlayer, killingQuests, 1);
        });
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (event.isCancelled()) {
      return;
    }
    final Location from = event.getFrom();
    final Location to = event.getTo();
    if (to == null) {
      return;
    }
    if (from.getBlockX() == to.getBlockX()
        && from.getBlockY() == to.getBlockY()
        && from.getBlockZ() == to.getBlockZ()) {
      return;
    }
    final Player player = event.getPlayer();
    handleRunningQuests(player, from, to);
    handleExploringQuests(player, to);
  }

  private void handleRunningQuests(Player player, Location from, Location to) {
    if (!player.isSprinting()) {
      return;
    }
    runAsynchronously(
        () -> {
          final double travelledDistance = from.distance(to);
          final Set<RunningQuest> runningQuests = questRepository.getRunningQuests();
          increaseProgress(player, runningQuests, travelledDistance);
        });
  }

  private void handleExploringQuests(Player player, Location to) {
    runAsynchronously(
        () -> {
          final Biome toBiome = to.getBlock().getBiome();
          final Set<ExploringQuest> exploringQuests = questRepository.getExploringQuests(toBiome);
          increaseProgress(player, exploringQuests, 1);
        });
  }

  private void runAsynchronously(Runnable runnable) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
  }

  private void increaseProgress(Player player, Set<? extends Quest> quests, double progress) {
    if (quests == null) {
      return;
    }
    final IQuestPlayer questPlayer = questPlayerRepository.get(player);
    if (questPlayer == null) {
      return;
    }
    for (Quest quest : quests) {
      questPlayer.increaseQuestProgress(quest, progress);
    }
  }
}
