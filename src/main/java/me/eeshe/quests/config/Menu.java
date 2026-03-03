package me.eeshe.quests.config;

import java.util.Map;
import me.eeshe.quests.config.registry.MenuRegistry;
import me.eeshe.quests.menu.holders.MenuHolder;
import me.eeshe.quests.util.PlaceholderUtil;
import me.eeshe.quests.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Menu {
  private static MenuConfig MENU_CONFIG;

  private final String key;
  private final int size;
  private final String title;
  private final ItemStack frameItem;
  private final ItemStack fillerItem;
  private final ItemStack previousPageItem;
  private final ItemStack nextPageItem;
  private final Map<String, Object> additionalConfigurations;

  public Menu(
      String key,
      int size,
      String title,
      ItemStack frameItem,
      ItemStack fillerItem,
      ItemStack previousPageItem,
      ItemStack nextPageItem,
      Map<String, Object> additionalConfigurations) {
    this.key = key;
    this.size = size;
    this.title = title;
    this.frameItem = frameItem;
    this.fillerItem = fillerItem;
    this.previousPageItem = previousPageItem;
    this.nextPageItem = nextPageItem;
    this.additionalConfigurations = additionalConfigurations;

    MenuRegistry.getInstance().add(this);
  }

  public static final void setMenuConfig(MenuConfig menuConfig) {
    MENU_CONFIG = menuConfig;
  }

  public Inventory createInventory(MenuHolder menuHolder, OfflinePlayer player) {
    final String title =
        PlaceholderUtil.formatPlaceholders(player, StringUtil.formatColor(this.title));

    return Bukkit.createInventory(menuHolder, size, title);
  }

  public Menu get() {
    return MENU_CONFIG.get(key);
  }

  public String getKey() {
    return key;
  }

  public int getSize() {
    return size;
  }

  public String getTitle() {
    return title;
  }

  public ItemStack getFrameItem() {
    return frameItem;
  }

  public ItemStack getFillerItem() {
    return fillerItem;
  }

  public ItemStack getPreviousPageItem() {
    return previousPageItem;
  }

  public ItemStack getNextPageItem() {
    return nextPageItem;
  }

  public Map<String, Object> getAdditionalConfigurations() {
    return additionalConfigurations;
  }
}
