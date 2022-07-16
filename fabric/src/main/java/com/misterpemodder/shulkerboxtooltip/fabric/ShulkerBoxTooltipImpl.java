package com.misterpemodder.shulkerboxtooltip.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ShulkerBoxTooltipImpl {
  /**
   * Implementation of {@link ShulkerBoxTooltip#isClient()}.
   */
  public static boolean isClient() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#getConfigDir()}.
   */
  public static Path getConfigDir() {
    return FabricLoader.getInstance().getConfigDir();
  }
}
