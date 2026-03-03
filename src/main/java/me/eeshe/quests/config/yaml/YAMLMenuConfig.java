package me.eeshe.quests.config.yaml;

import java.util.List;
import java.util.Map;
import me.eeshe.quests.config.Menu;
import me.eeshe.quests.config.MenuConfig;
import me.eeshe.quests.config.registry.MenuRegistry;
import me.eeshe.quests.util.LogUtil;
import me.eeshe.quests.util.YAMLUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class YAMLMenuConfig extends YAMLConfig implements MenuConfig {

  public YAMLMenuConfig(Plugin plugin) {
    super(plugin, null, "menus.yml");
  }

  @Override
  public void writeDefaults() {
    final FileConfiguration config = getConfig();
    for (Menu menu : MenuRegistry.getInstance().values()) {
      final String key = menu.getKey();

      config.addDefault(key + ".size", menu.getSize());
      config.addDefault(key + ".title", menu.getTitle());
      YAMLUtil.writeItemStack(config, key + ".frame-item", menu.getFrameItem());
      YAMLUtil.writeItemStack(config, key + ".filler-item", menu.getFillerItem());
      YAMLUtil.writeItemStack(config, key + ".previous-page-item", menu.getPreviousPageItem());
      YAMLUtil.writeItemStack(config, key + ".next-page-item", menu.getNextPageItem());
      config.addDefault(key + ".additional", menu.getAdditionalConfigurations());
    }
    super.writeDefaults();
  }

  @Override
  public Menu get(String key) {
    final ConfigurationSection menuSection = getConfig().getConfigurationSection(key);
    if (menuSection == null) {
      return null;
    }
    final int size = menuSection.getInt("size");
    if (size % 9 != 0 || size < 9 || size > 54) {
      LogUtil.warning(
          "Invalid menu size '"
              + size
              + "' configured for '"
              + key
              + "'. Size must be a multiple of 9 between 9 and 54.");
      return null;
    }
    final String title = menuSection.getString("title", "");
    final ItemStack frameItem = YAMLUtil.readItemStack(getConfig(), key + ".frame-item");
    final List<Integer> frameSlots = menuSection.getIntegerList("frame-slots");
    final ItemStack fillerItem = YAMLUtil.readItemStack(getConfig(), key + ".filler-item");
    final ItemStack previousPageItem =
        YAMLUtil.readItemStack(getConfig(), key + ".previous-page-item");
    final ItemStack nextPageItem = YAMLUtil.readItemStack(getConfig(), key + ".next-page-item");
    final Map<String, Object> additionalConfiguration =
        menuSection.getConfigurationSection("additional").getValues(false);

    return new Menu(
        key,
        size,
        title,
        frameItem,
        fillerItem,
        previousPageItem,
        nextPageItem,
        additionalConfiguration);
  }
}
