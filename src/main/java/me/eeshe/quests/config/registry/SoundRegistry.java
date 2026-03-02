package me.eeshe.quests.config.registry;

import me.eeshe.quests.config.Sound;

public class SoundRegistry extends ConfigRegistry<Sound> {
  private static final SoundRegistry INSTANCE = new SoundRegistry();

  public static final Sound TEST = new Sound("test", org.bukkit.Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);

  public static SoundRegistry getInstance() {
    return INSTANCE;
  }
}
