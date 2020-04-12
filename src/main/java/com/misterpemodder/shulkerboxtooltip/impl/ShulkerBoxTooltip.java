package com.misterpemodder.shulkerboxtooltip.impl;

import com.misterpemodder.shulkerboxtooltip.impl.network.client.C2SPacketTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ModInitializer;

public final class ShulkerBoxTooltip implements ModInitializer {
  public static String MOD_ID = "shulkerboxtooltip";
  public static final Logger LOGGER = LogManager.getLogger("ShulkerBoxTooltip");

  @Override
  public void onInitialize() {
    C2SPacketTypes.register();
  }
}
