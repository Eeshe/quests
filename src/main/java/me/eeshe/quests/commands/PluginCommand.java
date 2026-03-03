package me.eeshe.quests.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import me.eeshe.quests.config.Message;
import me.eeshe.quests.config.registry.MessageRegistry;
import me.eeshe.quests.config.registry.SoundRegistry;
import me.eeshe.quests.util.CommandUtil;
import me.eeshe.quests.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PluginCommand {
  private final Map<String, PluginCommand> subcommands;
  private final Map<Integer, BiFunction<CommandSender, String[], List<String>>> completions;
  private final Plugin plugin;
  private final PluginCommand parentPluginCommand;

  private String name;
  private String permission;
  private Message infoMessage;
  private Message usageMessage;
  private int argumentAmount;
  private boolean isConsoleCommand;
  private boolean isPlayerCommand;
  private boolean isUniversalCommand;

  public PluginCommand(Plugin plugin) {
    this.plugin = plugin;
    this.parentPluginCommand = null;
    this.subcommands = new LinkedHashMap<>();
    this.completions = new HashMap<>();
  }

  public PluginCommand(Plugin plugin, PluginCommand parentPluginCommand) {
    this.plugin = plugin;
    this.parentPluginCommand = parentPluginCommand;
    this.subcommands = new LinkedHashMap<>();
    this.completions = new HashMap<>();
  }

  public void execute(CommandSender sender, String[] args) {
    if (args.length < 1) {
      sendHelpMessage(sender);
      return;
    }
    // Compute parent commands to then find the subcommand name
    PluginCommand parentPluginCommand = getParentCommand();
    int parentCommandAmount = 0;
    while (parentPluginCommand != null) {
      parentCommandAmount += 1;
      parentPluginCommand = parentPluginCommand.getParentCommand();
    }
    String subcommandName = args[Math.min(args.length - 1, parentCommandAmount)].toLowerCase();
    PluginCommand subcommand = subcommands.get(subcommandName);
    if (subcommand == null) {
      sendHelpMessage(sender);
      return;
    }
    if (isPlayerCommand && !(sender instanceof Player)) {
      MessageRegistry.PLAYER_COMMAND.sendError(sender);
      return;
    }
    if (isConsoleCommand && sender instanceof Player) {
      MessageRegistry.CONSOLE_COMMAND.sendError(sender);
      return;
    }
    if (args.length - 1 < subcommand.getArgumentAmount()) {
      MessageRegistry.USAGE_TEXT.sendError(
          sender, Map.of("%usage%", subcommand.getUsageMessage().getValue()));
      return;
    }
    if (!subcommand.checkPermission(sender, true)) return;

    subcommand.execute(sender, Arrays.copyOfRange(args, parentCommandAmount + 1, args.length));
  }

  public void sendHelpMessage(CommandSender sender) {
    final StringBuilder finalMessage =
        new StringBuilder(MessageRegistry.HELP_TEXT_HEADER.getValue() + "\n");
    final String commandFormat = MessageRegistry.HELP_TEXT_COMMAND_FORMAT.getValue();
    List<? extends PluginCommand> commands =
        subcommands.values().stream()
            .filter(
                command ->
                    command.checkPermission(sender)
                        && command.getInfoMessage() != null
                        && command.getUsageMessage() != null)
            .toList();
    if (commands.isEmpty()) {
      MessageRegistry.NO_AVAILABLE_COMMANDS.sendError(sender);
      return;
    }
    for (int index = 0; index < commands.size(); index++) {
      PluginCommand penCommand = commands.get(index);
      if (penCommand.getInfoMessage() == null || penCommand.getUsageMessage() == null) continue;

      String commandText =
          commandFormat.replace("%usage%", penCommand.getUsageMessage().getValue());
      commandText = commandText.replace("%info%", penCommand.getInfoMessage().getValue());
      finalMessage.append(commandText);
      if (index == commands.size() - 1) continue;

      finalMessage.append("\n");
    }
    sender.sendMessage(StringUtil.formatColor(finalMessage.toString()));
    SoundRegistry.SUCCESS.play(sender);
  }

  public List<String> getCommandCompletions(CommandSender sender, String[] args) {
    if (!checkPermission(sender)) return new ArrayList<>();
    if (completions.isEmpty()) {
      List<String> completions = new ArrayList<>();
      for (PluginCommand pluginCommand : getSubcommands().values()) {
        if (!pluginCommand.checkPermission(sender)) continue;

        completions.add(pluginCommand.getName());
      }
      return completions;
    }

    return completions
        .getOrDefault(args.length - 1, (sender1, strings) -> new ArrayList<>())
        .apply(sender, args);
  }

  public boolean checkPermission(CommandSender sender) {
    return CommandUtil.hasPermission(sender, permission);
  }

  public boolean checkPermission(CommandSender sender, boolean sendNotification) {
    return CommandUtil.hasPermission(sender, permission, sendNotification);
  }

  public Plugin getPlugin() {
    return plugin;
  }

  public PluginCommand getParentCommand() {
    return parentPluginCommand;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Message getInfoMessage() {
    return infoMessage;
  }

  public void setInfoMessage(Message infoMessage) {
    this.infoMessage = infoMessage;
  }

  public Message getUsageMessage() {
    return usageMessage;
  }

  public void setUsageMessage(Message usageMessage) {
    this.usageMessage = usageMessage;
  }

  public int getArgumentAmount() {
    return argumentAmount;
  }

  public void setArgumentAmount(int argumentAmount) {
    this.argumentAmount = argumentAmount;
  }

  public boolean isConsoleCommand() {
    return isConsoleCommand;
  }

  public void setConsoleCommand(boolean consoleCommand) {
    isConsoleCommand = consoleCommand;
  }

  public boolean isPlayerCommand() {
    return isPlayerCommand;
  }

  public void setPlayerCommand(boolean playerCommand) {
    isPlayerCommand = playerCommand;
  }

  public boolean isUniversalCommand() {
    return isUniversalCommand;
  }

  public void setUniversalCommand(boolean universalCommand) {
    isUniversalCommand = universalCommand;
  }

  public Map<String, PluginCommand> getSubcommands() {
    return subcommands;
  }

  public void setSubcommands(List<PluginCommand> pluginCommands) {
    for (PluginCommand pluginCommand : pluginCommands) {
      subcommands.put(pluginCommand.getName(), pluginCommand);
    }
  }

  public PluginCommand getSubcommand(String subcommandName) {
    return subcommands.get(subcommandName);
  }

  public void setCompletions(
      Map<Integer, BiFunction<CommandSender, String[], List<String>>> completions) {
    this.completions.clear();
    this.completions.putAll(completions);
  }
}
