package me.eeshe.quests.config.registry;

import java.util.List;
import me.eeshe.quests.config.Menu;
import me.eeshe.quests.util.ItemBuilder;
import org.bukkit.Material;

public class MenuRegistry extends ConfigRegistry<Menu> {
  private static final MenuRegistry INSTANCE = new MenuRegistry();

  public static final Menu QUEST_LIST =
      new Menu(
          "quest-ist",
          54,
          "Quest List",
          ItemBuilder.builder().material(Material.CYAN_STAINED_GLASS_PANE).displayName(" ").build(),
          List.of(-1),
          ItemBuilder.builder()
              .material(Material.BLACK_STAINED_GLASS_PANE)
              .displayName(" ")
              .build(),
          ItemBuilder.builder().material(Material.ARROW).displayName("&7Previous Page").build(),
          ItemBuilder.builder().material(Material.ARROW).displayName("&7Next Page").build());

  public static MenuRegistry getInstance() {
    return INSTANCE;
  }
}
