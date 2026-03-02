package me.eeshe.quests.config.yaml;

import me.eeshe.quests.config.Sound;
import me.eeshe.quests.config.SoundConfig;
import me.eeshe.quests.config.registry.SoundRegistry;
import me.eeshe.quests.util.LogUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class YAMLSoundConfig extends YAMLConfig implements SoundConfig {

  public YAMLSoundConfig(Plugin plugin) {
    super(plugin, null, "sounds.yml");
  }

  @Override
  public void writeDefaults() {
    final FileConfiguration config = getConfig();
    for (Sound sound : SoundRegistry.getInstance().values()) {
      config.addDefault(sound.getKey() + ".enabled", sound.getIsEnabled());
      config.addDefault(sound.getKey() + ".sound", sound.getSound().name());
      config.addDefault(sound.getKey() + ".volume", sound.getVolume());
      config.addDefault(sound.getKey() + ".pitch", sound.getPitch());
    }
    super.writeDefaults();
  }

  @Override
  public Sound get(String key) {
    final ConfigurationSection soundSection = getConfig().getConfigurationSection(key);
    if (soundSection == null) {
      LogUtil.warning("Invalid configuration for sound '" + key + "'.");
      return null;
    }
    final boolean enabled = soundSection.getBoolean("enabled");

    final String soundName = soundSection.getString("sound");
    final org.bukkit.Sound sound;
    try {
      sound = org.bukkit.Sound.valueOf(soundName);
    } catch (Exception e) {
      LogUtil.warning("Unknown sound '" + soundName + "' configured for '" + key + "'.");
      return null;
    }
    final float volume = (float) soundSection.getDouble("volume");
    final float pitch = (float) soundSection.getDouble("pitch");

    return new Sound(key, enabled, sound, volume, pitch);
  }
}
