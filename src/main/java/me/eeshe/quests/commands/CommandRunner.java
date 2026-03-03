package me.eeshe.quests.commands;

import java.util.Map;
import me.eeshe.quests.Quests;
import me.eeshe.quests.config.registry.MessageRegistry;
import me.eeshe.quests.util.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandRunner implements CommandExecutor {
  private final Quests plugin;

  public CommandRunner(Quests plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    String commandName = cmd.getName();
    PluginCommand pluginCommand = plugin.getCommands().get(commandName);
    if (pluginCommand == null) return false;
    if (args.length < pluginCommand.getArgumentAmount()) {
      if (pluginCommand.getUsageMessage() == null) {
        pluginCommand.sendHelpMessage(sender);
        return true;
      }
      MessageRegistry.USAGE_TEXT.sendError(
          sender, Map.of("%usage%", pluginCommand.getUsageMessage().getValue()));
      return true;
    }
    if (!CommandUtil.hasPermission(sender, pluginCommand.getPermission(), true)) return true;

    pluginCommand.execute(sender, args);
    return true;
  }
}
