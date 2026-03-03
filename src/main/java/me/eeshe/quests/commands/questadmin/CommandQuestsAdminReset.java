package me.eeshe.quests.commands.questadmin;

import java.util.Map;
import me.eeshe.quests.Quests;
import me.eeshe.quests.commands.PluginCommand;
import me.eeshe.quests.config.registry.MessageRegistry;
import me.eeshe.quests.model.questplayer.IQuestPlayer;
import me.eeshe.quests.model.quests.Quest;
import me.eeshe.quests.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandQuestsAdminReset extends PluginCommand {

  public CommandQuestsAdminReset(Plugin plugin, PluginCommand parentPluginCommand) {
    super(plugin, parentPluginCommand);

    setName("reset");
    setPermission("quests.admin");
    setInfoMessage(MessageRegistry.RESET_COMMAND_INFO);
    setUsageMessage(MessageRegistry.RESET_COMMAND_USAGE);
    setArgumentAmount(2);
    setUniversalCommand(true);
    setCompletions(
        Map.ofEntries(
            Map.entry(0, (sender, args) -> null),
            Map.entry(
                1,
                (sender, args) ->
                    ((Quests) getPlugin())
                        .getQuestRepository().getAll().stream().map(Quest::getId).toList())));
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    final Player target = CommandUtil.getOnlineTarget(sender, args[0]);
    if (target == null) {
      return;
    }
    final String questId = args[1];
    final Quest quest = ((Quests) getPlugin()).getQuestRepository().get(questId);
    if (quest == null) {
      MessageRegistry.QUEST_NOT_FOUND.sendError(sender, Map.of("%quest%", questId));
      return;
    }
    final IQuestPlayer questPlayer = ((Quests) getPlugin()).getQuestPlayerRepository().get(target);
    questPlayer.resetQuest(quest);
    MessageRegistry.RESET_COMMAND_SUCCESS.sendSuccess(
        sender,
        Map.ofEntries(
            Map.entry("%target%", target.getDisplayName()), Map.entry("%quest%", questId)));
  }
}
