package me.eeshe.quests.model.rewards;

import java.util.List;
import me.eeshe.quests.util.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandsReward implements Reward {
  private final List<String> commands;

  public CommandsReward(List<String> commands) {
    this.commands = commands;
  }

  @Override
  public void apply(Player player) {
    for (String command : commands) {
      Bukkit.dispatchCommand(
          Bukkit.getConsoleSender(), PlaceholderUtil.formatPlaceholders(player, command));
    }
  }

  public List<String> getCommands() {
    return commands;
  }
}
