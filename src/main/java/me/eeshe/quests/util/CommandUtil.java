package me.eeshe.quests.util;

import java.util.Map;
import me.eeshe.quests.config.registry.MessageRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUtil {

  public static boolean hasPermission(CommandSender commandSender, String permission) {
    return hasPermission(commandSender, permission, false);
  }

  public static boolean hasPermission(
      CommandSender commandSender, String permission, boolean sendMessage) {
    if (permission == null || permission.isEmpty()) return true;
    if (commandSender.hasPermission(permission)) return true;
    if (sendMessage) {
      MessageRegistry.NO_PERMISSION.sendError(commandSender);
    }
    return false;
  }

  public static Player getOnlineTarget(CommandSender sender, String targetName) {
    Player target = Bukkit.getPlayerExact(targetName);
    if (target == null) {
      MessageRegistry.PLAYER_NOT_FOUND.sendError(sender, Map.of("%player%", targetName));
      return null;
    }
    return target;
  }
}
