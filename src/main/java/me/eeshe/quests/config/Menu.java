package me.eeshe.quests.config;

public class Menu {
  private static MenuConfig MENU_CONFIG;

  public static void setMenuConfig(MenuConfig menuConfig) {
    if (MENU_CONFIG != null) {
      return;
    }
    MENU_CONFIG = menuConfig;
  }
}
