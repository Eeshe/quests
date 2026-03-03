package me.eeshe.quests.commands.questadmin;

import me.eeshe.quests.Quests;
import me.eeshe.quests.commands.PluginCommand;
import me.eeshe.quests.config.registry.MessageRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandQuestsAdminReload extends PluginCommand {

  public CommandQuestsAdminReload(Plugin plugin, PluginCommand parentPluginCommand) {
    super(plugin, parentPluginCommand);

    setName("reload");
    setPermission("quests.admin");
    setInfoMessage(MessageRegistry.RELOAD_COMMAND_INFO);
    setUsageMessage(MessageRegistry.RELOAD_COMMAND_USAGE);
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    ((Quests) getPlugin()).reload();
    MessageRegistry.RELOAD_COMMAND_SUCCESS.sendSuccess(sender);
  }
}
