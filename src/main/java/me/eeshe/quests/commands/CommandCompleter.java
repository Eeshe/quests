package me.eeshe.quests.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.eeshe.quests.Quests;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandCompleter implements TabCompleter {
  private final Quests plugin;

  public CommandCompleter(Quests plugin) {
    this.plugin = plugin;
  }

  public List<String> onTabComplete(
      CommandSender sender, Command cmd, String label, String[] args) {
    PluginCommand command = plugin.getCommands().get(cmd.getName());
    if (command == null) return new ArrayList<>();
    if (!command.checkPermission(sender)) return new ArrayList<>();

    if (!command.getSubcommands().isEmpty() && args.length > 1) {
      command = command.getSubcommand(args[0]);
      args = Arrays.copyOfRange(args, 1, args.length);
    }
    if (command == null) return new ArrayList<>();
    List<String> completions = command.getCommandCompletions(sender, args);
    if (completions == null) return null;

    return getCompletion(completions, args);
  }

  private List<String> getCompletion(List<String> arguments, String[] args) {
    if (arguments == null) return null;

    List<String> results = new ArrayList<>();
    for (String argument : arguments) {
      if (!argument.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) continue;

      results.add(argument);
    }
    return results;
  }
}
