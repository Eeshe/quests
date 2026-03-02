package me.eeshe.quests.config;

import me.eeshe.quests.config.registry.SoundRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sound {
  private static SoundConfig SOUND_CONFIG;

  private final String key;
  private final boolean defaultEnabled;
  private final org.bukkit.Sound defaultSound;
  private final float defaultVolume;
  private final float defaultPitch;

  public Sound(
      String key,
      boolean defaultEnabled,
      org.bukkit.Sound defaultSound,
      float defaultVolume,
      float defaultPitch) {
    this.key = key;
    this.defaultEnabled = defaultEnabled;
    this.defaultSound = defaultSound;
    this.defaultVolume = defaultVolume;
    this.defaultPitch = defaultPitch;

    SoundRegistry.getInstance().add(this);
  }

  public Sound(String key, org.bukkit.Sound defaultSound, float defaultVolume, float defaultPitch) {
    this.key = key;
    this.defaultEnabled = true;
    this.defaultSound = defaultSound;
    this.defaultVolume = defaultVolume;
    this.defaultPitch = defaultPitch;

    SoundRegistry.getInstance().add(this);
  }

  public static void setSoundConfig(SoundConfig soundConfig) {
    SOUND_CONFIG = soundConfig;
  }

  public void play(final CommandSender sender) {
    if (!(sender instanceof Player player)) {
      return;
    }
    final Sound sound = get();
    if (sound == null) {
      return;
    }
    player.playSound(
        player, sound.getDefaultSound(), sound.getDefaultVolume(), sound.getDefaultPitch());
  }

  private Sound get() {
    return SOUND_CONFIG.get(key);
  }

  public String getKey() {
    return key;
  }

  public boolean getDefaultEnabled() {
    return defaultEnabled;
  }

  public org.bukkit.Sound getDefaultSound() {
    return defaultSound;
  }

  public float getDefaultVolume() {
    return defaultVolume;
  }

  public float getDefaultPitch() {
    return defaultPitch;
  }
}
