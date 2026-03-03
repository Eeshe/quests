package me.eeshe.quests.commands.questadmin;

import java.util.List;
import me.eeshe.quests.commands.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandQuestsAdmin extends PluginCommand {

  public CommandQuestsAdmin(Plugin plugin) {
    super(plugin);

    setName("questsadmin");
    setPermission("quests.admin");
    setSubcommands(
        List.of(
            new CommandQuestsAdminReload(plugin, this),
            new CommandQuestsAdminSetProgress(plugin, this),
            new CommandQuestsAdminReset(plugin, this)));
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    super.execute(sender, args);
  }
}
