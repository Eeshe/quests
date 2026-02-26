package me.eeshe.quests.config.yaml;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import me.eeshe.quests.config.Config;
import me.eeshe.quests.util.LogUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class YAMLConfig implements Config {
  private final Plugin plugin;
  private final File file;
  private FileConfiguration config;

  public YAMLConfig(Plugin plugin, String directoryName, String fileName) {
    this.plugin = plugin;
    this.file = new File(plugin.getDataFolder(), computeFilePath(directoryName, fileName));
  }

  private String computeFilePath(String directoryName, String fileName) {
    String filePath = "";
    if (directoryName != null) {
      filePath += directoryName;
      if (!filePath.endsWith("/")) {
        filePath += "/";
      }
    }
    filePath += fileName;

    return filePath;
  }

  private boolean ensureFileExists(File file) {
    if (file.exists()) {
      return true;
    }
    try {
      Files.createParentDirs(file);
      return file.createNewFile();
    } catch (IOException e) {
      LogUtil.warning("Couldn't create YAML file '" + file + "'. Error message: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void load() {
    if (!ensureFileExists(file)) {
      this.config = null;
      return;
    }
    this.config = YamlConfiguration.loadConfiguration(file);
    writeDefaults();
  }

  @Override
  public void writeDefaults() {
    save();
  }

  @Override
  public void save() {
    try {
      config.options().copyDefaults(true);
      config.save(file);
    } catch (IOException e) {
      plugin
          .getLogger()
          .severe("Error saving config to '" + file + "'. Error message: " + e.getMessage());
    }
  }

  public FileConfiguration getConfig() {
    if (config == null) {
      load();
    }
    return config;
  }
}
