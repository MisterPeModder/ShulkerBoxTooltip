package com.misterpemodder.shulkerboxtooltip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.loader.api.FabricLoader;

public final class Configuration {
  private static final Logger LOGGER = LogManager.getLogger("ShulkerBoxTooltip");
  private static boolean lockPreview = false;
  private static boolean swapModes = false;
  private static boolean enablePreview = true;
  private static ShulkerBoxTooltipType tooltipType = ShulkerBoxTooltipType.MOD;

  public static void initConfiguration() {
    File cfgDirectory =
        new File(FabricLoader.getInstance().getConfigDirectory(), "shulkerboxtooltip");
    File cfg = new File(cfgDirectory, "config.properties");
    Properties properties = new Properties();

    if (cfgDirectory.exists() || cfgDirectory.mkdirs()) {
      if (!cfg.exists()) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
            ShulkerBoxTooltip.class.getResourceAsStream("/config.properties")))) {
          try (PrintWriter pw = new PrintWriter(new FileOutputStream(cfg))) {
            String line;
            while ((line = br.readLine()) != null)
              pw.println(line);
          } catch (IOException err) {
            LOGGER.error("Could not write default configuration", err);
          }
        } catch (IOException err) {
          LOGGER.error("Could not read default configuration", err);
        }
      }

      try (InputStream in = new FileInputStream(cfg)) {
        properties.load(in);
      } catch (IOException err) {
        LOGGER.error("Could not read configuration", err);
      }
    } else {
      LOGGER.error("Could not create configuration, using default.");
    }

    lockPreview = parseBoolean(properties, "preview.position.lock", false);
    swapModes = parseBoolean(properties, "preview.modes.swap", false);
    enablePreview = parseBoolean(properties, "preview.enabled", true);
    tooltipType = parseTooltipType(properties, "tooltip.type", ShulkerBoxTooltipType.MOD);
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

  public static boolean isPreviewEnabled() {
    return enablePreview;
  }

  public static boolean isPreviewLocked() {
    return lockPreview;
  }

  public static boolean areModesSwapped() {
    return swapModes;
  }

  public static ShulkerBoxTooltipType getTooltipType() {
    return tooltipType;
  }

  public static enum ShulkerBoxTooltipType {
    VANILLA, MOD, NONE;
  }
}
