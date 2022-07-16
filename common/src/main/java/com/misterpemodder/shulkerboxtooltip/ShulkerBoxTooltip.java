package com.misterpemodder.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;

public class ShulkerBoxTooltip {
  public static final String MOD_ID = "shulkerboxtooltip";
  public static final String MOD_NAME = "ShulkerBoxTooltip";
  public static final Logger LOGGER = LogManager.getFormatterLogger("ShulkerBoxTooltip");

  /**
   * The active config object, some of its properties are synced with the server.
   */
  public static Configuration config;
  /**
   * the actual config object, its values are never synced.
   */
  public static Configuration savedConfig;


  public static void init() {
    savedConfig = ConfigurationHandler.register();
    config = ConfigurationHandler.copyOf(savedConfig);
  }

  /**
   * @return Whether the current environment type (or Dist in forge terms) is the client.
   */
  @ExpectPlatform
  @SuppressWarnings("Contract")
  @Contract(value = " -> _", pure = true)
  public static boolean isClient() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.isClient()");
  }

  /**
   * Get the current directory for game configuration files.
   *
   * @return the configuration directory.
   */
  @ExpectPlatform
  @SuppressWarnings("Contract")
  @Contract(value = " -> _", pure = true)
  public static Path getConfigDir() {
    throw new AssertionError("Missing implementation of ShulkerBoxTooltip.getConfigDir()");
  }
}
