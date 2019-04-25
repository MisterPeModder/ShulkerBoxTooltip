package com.misterpemodder.shulkerboxtooltip;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import com.misterpemodder.shulkerboxtooltip.Configuration.ShulkerBoxTooltipType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import me.sargunvohra.mcmods.autoconfig1.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1.ConfigHolder;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Handles pre-1.2 config files backwards compat.
 */
public final class LegacyConfiguration {
  private static final Logger LOGGER = LogManager.getLogger("ShulkerBoxTooltip");

  public static void updateConfig() {
    Path configDir =
        Paths.get(FabricLoader.getInstance().getConfigDirectory().toString(), "shulkerboxtooltip");
    if (Files.exists(configDir)) {
      Path configFile = configDir.resolve("config.properties");
      LOGGER.info("Found shulkerboxtooltip legacy config directory, removing after conversion...");
      if (Files.exists(configFile)) {
        LOGGER.info("Found shulkerboxtooltip legacy config file, converting and removing...");

        try (Reader in = Files.newBufferedReader(configFile)) {
          Properties properties = new Properties();
          properties.load(in);
          ConfigHolder<Configuration> configHolder =
              AutoConfig.getConfigHolder(Configuration.class);
          Configuration config = configHolder.getConfig();
          config.main.enablePreview = parseBoolean(properties, "preview.enabled", true);
          config.main.lockPreview = parseBoolean(properties, "preview.position.lock", false);
          config.main.swapModes = parseBoolean(properties, "preview.modes.swap", false);
          config.main.tooltipType =
              parseTooltipType(properties, "tooltip.type", ShulkerBoxTooltipType.MOD);
          try {
            configHolder.getClass().getMethod("save").invoke(configHolder);
          } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
              | NoSuchMethodException | SecurityException e) {
            LOGGER.error("Failed to convert legacy config file to new format, canceling...", e);
            return;
          }
        } catch (IOException e) {
          LOGGER.error("Could not read legacy configuration", e);
        }

        try {
          Files.delete(configFile);
        } catch (IOException e) {
          LOGGER.error("Failed to delete legacy config file!", e);
        }
      }
      try {
        Files.delete(configDir);
      } catch (IOException e) {
        LOGGER.error("Failed to delete legacy config directory!", e);
      }
    }
  }

  private static boolean parseBoolean(Properties properties, String key, boolean defaultValue) {
    String value = properties.getProperty(key);
    return value != null ? Boolean.parseBoolean(value) : defaultValue;
  }

  private static ShulkerBoxTooltipType parseTooltipType(Properties properties, String key,
      ShulkerBoxTooltipType defaultType) {
    String value = properties.getProperty(key);
    if (value != null) {
      try {
        int type = Integer.parseInt(value);
        if (type < 0 || type >= ShulkerBoxTooltipType.values().length) {
          LOGGER
              .error("Invalid shulker box tooltip type, expected integer between 0 (inclusive) and "
                  + ShulkerBoxTooltipType.values().length + " (inclusive).");
          return defaultType;
        }
        return ShulkerBoxTooltipType.values()[type];
      } catch (NumberFormatException e) {
        LOGGER.error("Invalid value for key '" + key + "' in config, expected integer.");
      }
    }
    return defaultType;
  }
}
