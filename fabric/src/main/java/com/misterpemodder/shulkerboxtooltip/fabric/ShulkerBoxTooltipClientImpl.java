package com.misterpemodder.shulkerboxtooltip.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipComponent;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

@Environment(EnvType.CLIENT)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ShulkerBoxTooltipClient.init();

    // PreviewTooltipData -> PreviewTooltipComponent conversion
    TooltipComponentCallback.EVENT.register(data -> {
      if (data instanceof PreviewTooltipData previewData)
        return new PreviewTooltipComponent(previewData);
      return null;
    });
  }
}
