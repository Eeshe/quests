package me.eeshe.quests.commands;

import me.eeshe.quests.Quests;
import me.eeshe.quests.config.registry.SoundRegistry;
import me.eeshe.quests.menu.QuestListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandQuests extends PluginCommand {

  public CommandQuests(Plugin plugin) {
    super(plugin);

    setName("quests");
    setArgumentAmount(0);
    setPlayerCommand(true);
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    final Player player = (Player) sender;

    player.openInventory(new QuestListMenu(player, 1, (Quests) getPlugin()).create());
    SoundRegistry.QUEST_LIST_MENU_OPEN.play(player);
  }
}
