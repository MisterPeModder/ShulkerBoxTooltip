package com.misterpemodder.shulkerboxtooltip.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipComponent;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipData;
import io.github.cottonmc.cotton.gui.client.LibGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient
    implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ShulkerBoxTooltipClient.init();

    // PreviewTooltipData -> PreviewTooltipComponent conversion
    TooltipComponentCallback.EVENT.register(data -> {
      if (data instanceof PreviewTooltipData previewData)
        return new PreviewTooltipComponent(previewData);
      return null;
    });

    // LibGUI integration
    if (FabricLoader.getInstance().isModLoaded("libgui")) {
      ShulkerBoxTooltip.LOGGER.info(
          "[" + ShulkerBoxTooltip.MOD_NAME + "] Found LibGui, enabling integration");
      darkModeSupplier = LibGui::isDarkMode;
    }
  }
}
