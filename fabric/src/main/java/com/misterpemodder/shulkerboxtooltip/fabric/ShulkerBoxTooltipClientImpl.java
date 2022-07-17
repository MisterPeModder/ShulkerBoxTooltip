package com.misterpemodder.shulkerboxtooltip.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import io.github.cottonmc.cotton.gui.client.LibGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient
    implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    super.onInitializeClient();
    if (FabricLoader.getInstance().isModLoaded("libgui")) {
      ShulkerBoxTooltip.LOGGER.info(
          "[" + ShulkerBoxTooltip.MOD_NAME + "] Found LibGui, enabling integration");
      darkModeSupplier = LibGui::isDarkMode;
    }
  }
}
