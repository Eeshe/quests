package me.eeshe.quests.config.registry;

import me.eeshe.quests.config.Sound;

public class SoundRegistry extends ConfigRegistry<Sound> {
  private static final SoundRegistry INSTANCE = new SoundRegistry();

  public static final Sound SUCCESS =
      new Sound("success", true, org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.6F, 1.6F);
  public static final Sound ERROR =
      new Sound("error", true, org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 0.6F, 0.6F);

  public static final Sound QUEST_LIST_MENU_OPEN =
      new Sound("quest-list-menu-open", true, org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 0.6F, 1.2F);
  public static final Sound PAGE_CHANGE =
      new Sound("page-change", true, org.bukkit.Sound.ITEM_BOOK_PAGE_TURN, 0.6F, 1.2F);

  public static SoundRegistry getInstance() {
    return INSTANCE;
  }
}
