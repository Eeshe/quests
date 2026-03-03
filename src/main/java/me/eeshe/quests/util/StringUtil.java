package me.eeshe.quests.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;

public class StringUtil {

  public static String formatColor(String text) {
    Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      String hexCode = text.substring(matcher.start(), matcher.end());
      String replaceSharp = hexCode.replace('#', 'x');

      char[] ch = replaceSharp.toCharArray();
      StringBuilder builder = new StringBuilder("");
      for (char c : ch) {
        builder.append("&" + c);
      }

      text = text.replace(hexCode, builder.toString());
      matcher = pattern.matcher(text);
    }
    return ChatColor.translateAlternateColorCodes('&', text);
  }

  public static String formatEnum(Enum<?> enumValue) {
    return formatEnum(enumValue.name());
  }

  public static String formatEnum(String text) {
    String replaced = text.replace('_', ' ');
    String[] words = replaced.split(" ");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      if (word.isEmpty()) continue;
      String cap = Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
      sb.append(cap);
      if (i < words.length - 1) sb.append(" ");
    }
    return sb.toString();
  }
}
