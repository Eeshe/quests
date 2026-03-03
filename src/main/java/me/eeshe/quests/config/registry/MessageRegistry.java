package me.eeshe.quests.config.registry;

import me.eeshe.quests.config.Message;

public class MessageRegistry extends ConfigRegistry<Message> {
  private static final MessageRegistry INSTANCE = new MessageRegistry();

  public static final Message UNKNOWN_COMMAND =
      new Message(
          "unknown-command",
          "&cUnknown command. Run &l/quests &chelp to see the full list of commands.");
  public static final Message PLAYER_COMMAND =
      new Message("player-command", "Not available for consoles.");
  public static final Message CONSOLE_COMMAND =
      new Message("console-command", "Not available for players.");
  public static final Message NO_PERMISSION =
      new Message("no-permission", "&cYou don't have permissions to run this command.");
  public static final Message USAGE_TEXT = new Message("usage-text", "&cUsage: %usage%.");
  public static final Message PLAYER_NOT_FOUND =
      new Message("player-not-found", "&cUnknown player &l%player%&c.");

  public static final Message HELP_COMMAND_INFO =
      new Message("help-command-info", "Displays this list.");
  public static final Message HELP_COMMAND_USAGE =
      new Message("help-command-usage", "/quests help");
  public static final Message HELP_TEXT_HEADER = new Message("help-text-header", "&e&lCommands");
  public static final Message HELP_TEXT_COMMAND_FORMAT =
      new Message("help-text-command-format", "&b%usage% &e- &9%info%");
  public static final Message NO_AVAILABLE_COMMANDS =
      new Message("no-available-commands", "&cYou don't have access to any commands.");

  public static final Message RELOAD_COMMAND_INFO =
      new Message("reload-command-info", "Reloads the plugin's configuration file.");
  public static final Message RELOAD_COMMAND_USAGE =
      new Message("reload-command-usage", "/quests reload");
  public static final Message RELOAD_COMMAND_SUCCESS =
      new Message("reload-command-success", "&aConfiguration successfully reloaded.");

  public static final Message SET_PROGRESS_COMMAND_INFO =
      new Message(
          "set-progress-command-info",
          "Sets the progress of the specified quest for the selected player.");
  public static final Message SET_PROGRESS_COMMAND_USAGE =
      new Message("set-progress-command-usage", "/quests setprogress <Player> <Quest> <Progress>");
  public static final Message QUEST_NOT_FOUND =
      new Message("quest-not-found", "&cQuest &l%quest%&c not found.");
  public static final Message INVALID_PROGRESS =
      new Message("invalid-progress", "&cProgress must be a number higher or equal to 0.");
  public static final Message SET_PROGRESS_COMMAND_SUCCESS =
      new Message(
          "set-progress-command-success",
          "&a&l%target%'s &a&l%quest%&a progress successfully set to &l%progress%&a.");

  public static final Message RESET_COMMAND_INFO =
      new Message(
          "reset-command-info",
          "Resets the progress of the specified quest for the selected player.");
  public static final Message RESET_COMMAND_USAGE =
      new Message("reset-command-usage", "/quests reset <Player> <Quest>");
  public static final Message RESET_COMMAND_SUCCESS =
      new Message(
          "reset-command-success", "&aSuccessfully reset quest &l%quest%&a for &l%target%&a.");

  public static MessageRegistry getInstance() {
    return INSTANCE;
  }
}
