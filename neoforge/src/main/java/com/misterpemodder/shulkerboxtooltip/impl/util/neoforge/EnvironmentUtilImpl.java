package com.misterpemodder.shulkerboxtooltip.impl.util.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

public class EnvironmentUtilImpl {

  private EnvironmentUtilImpl() {
  }

  /**
   * Implementation of {@link com.misterpemodder.shulkerboxtooltip.impl.util.EnvironmentUtil#isClient()}.
   */
  public static boolean isClient() {
    return FMLEnvironment.getDist() == Dist.CLIENT;
  }
}
