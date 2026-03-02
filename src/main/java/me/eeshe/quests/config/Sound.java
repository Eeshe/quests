package me.eeshe.quests.config;

import me.eeshe.quests.config.registry.SoundRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sound {
  private static SoundConfig SOUND_CONFIG;

  private final String key;
  private final boolean isEnabled;
  private final org.bukkit.Sound sound;
  private final float volume;
  private final float pitch;

  public Sound(
      String key,
      boolean defaultEnabled,
      org.bukkit.Sound defaultSound,
      float defaultVolume,
      float defaultPitch) {
    this.key = key;
    this.isEnabled = defaultEnabled;
    this.sound = defaultSound;
    this.volume = defaultVolume;
    this.pitch = defaultPitch;

    SoundRegistry.getInstance().add(this);
  }

  public Sound(String key, org.bukkit.Sound defaultSound, float defaultVolume, float defaultPitch) {
    this.key = key;
    this.isEnabled = true;
    this.sound = defaultSound;
    this.volume = defaultVolume;
    this.pitch = defaultPitch;

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
    player.playSound(player, sound.getSound(), sound.getVolume(), sound.getPitch());
  }

  private Sound get() {
    return SOUND_CONFIG.get(key);
  }

  public String getKey() {
    return key;
  }

  public boolean getIsEnabled() {
    return isEnabled;
  }

  public org.bukkit.Sound getSound() {
    return sound;
  }

  public float getVolume() {
    return volume;
  }

  public float getPitch() {
    return pitch;
  }
}
