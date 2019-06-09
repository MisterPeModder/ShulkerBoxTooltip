package com.misterpemodder.shulkerboxtooltip.api;

import java.util.List;
import java.util.Map;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import net.minecraft.item.Item;

/**
 * Implement this interface and use this implementation as your entrypoint.
 * @since 1.3.0
 */
public interface ShulkerBoxTooltipApi {
  /**
   * @return The owner mod of this API implementation.
   * @since 1.3.0
   */
  String getModId();

  /**
   * Registers a preview provider for a list of items.
   * @param previewProviders A PreviewProviders to list of Items map.
   * @since 1.3.0
   */
  void registerProviders(Map<PreviewProvider, List<Item>> previewProviders);
}
