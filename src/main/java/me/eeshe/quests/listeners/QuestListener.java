package me.eeshe.quests.listeners;

import java.util.List;
import me.eeshe.quests.Quests;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.model.quests.KillingQuest;
import me.eeshe.quests.model.quests.MiningQuest;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.repository.QuestPlayerRepository;
import me.eeshe.quests.repository.QuestRepository;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
          final List<MiningQuest> miningQuests = questRepository.getMiningQuests(blockType);
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
          final List<KillingQuest> killingQuests = questRepository.getKillingQuests(entityType);
          increaseProgress(damagerPlayer, killingQuests, 1);
        });
  }

  private void runAsynchronously(Runnable runnable) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
  }

  private void increaseProgress(Player player, List<? extends Quest> quests, int progress) {
    final IQuestPlayer questPlayer = questPlayerRepository.get(player);
    if (questPlayer == null) {
      return;
    }
    for (Quest quest : quests) {
      questPlayer.increaseQuestProgress(quest, progress);
    }
  }
}
