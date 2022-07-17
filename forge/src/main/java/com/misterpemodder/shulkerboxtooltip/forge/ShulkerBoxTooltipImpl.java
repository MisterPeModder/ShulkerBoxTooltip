package com.misterpemodder.shulkerboxtooltip.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod(ShulkerBoxTooltip.MOD_ID)
public class ShulkerBoxTooltipImpl extends ShulkerBoxTooltip {
  public ShulkerBoxTooltipImpl() {
    super.onInitialize();
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#isClient()}.
   */
  public static boolean isClient() {
    return FMLEnvironment.dist == Dist.CLIENT;
  }

  /**
   * Implementation of {@link ShulkerBoxTooltip#getConfigDir()}.
   */
  public static Path getConfigDir() {
    return FMLPaths.CONFIGDIR.get();
  }
}
