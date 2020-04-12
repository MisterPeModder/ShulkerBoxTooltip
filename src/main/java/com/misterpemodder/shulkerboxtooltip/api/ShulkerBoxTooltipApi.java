package com.misterpemodder.shulkerboxtooltip.api;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Implement this interface and use this as your entrypoint.
 * @since 1.3.0
 */
public interface ShulkerBoxTooltipApi {
  /**
   * @param stack The stack
   * @return the associated {@link PreviewProvider} for the passed {@link ItemStack}.
   * @since 1.5.0
   */
  @Nullable
  static PreviewProvider getPreviewProviderForStack(ItemStack stack) {
    Map<Item, PreviewProvider> previewItems = ShulkerBoxTooltip.getPreviewItems();

    return previewItems == null ? null : previewItems.get(stack.getItem());
  }

  /**
   * Is there a preview available for the given stack?
   * 
   * @param stack The stack to check.
   * @return true if there is a preview available
   * @since 1.5.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isPreviewAvailable(ItemStack stack) {
    if (ShulkerBoxTooltip.config.main.enablePreview) {
      PreviewProvider provider = getPreviewProviderForStack(stack);

      return provider != null && provider.shouldDisplay(stack) && ShulkerBoxTooltipApi
          .getCurrentPreviewType(provider.isFullPreviewAvailable(stack)) != PreviewType.NO_PREVIEW;
    }
    return false;
  }

  /**
   * @param hasFullPreviewMode Is the full preview mode available?
   * @return The shulker box tooltip type depending of which keys are pressed.
   * @since 1.5.0
   */
  @Environment(EnvType.CLIENT)
  static PreviewType getCurrentPreviewType(boolean hasFullPreviewMode) {
    boolean shouldDisplay = ShulkerBoxTooltipClient.shouldDisplayPreview();

    if (shouldDisplay && !hasFullPreviewMode) {
      return PreviewType.COMPACT;
    }
    if (ShulkerBoxTooltip.config.main.swapModes) {
      if (shouldDisplay)
        return Screen.hasAltDown() ? PreviewType.COMPACT : PreviewType.FULL;
    } else {
      if (shouldDisplay)
        return Screen.hasAltDown() ? PreviewType.FULL : PreviewType.COMPACT;
    }
    return PreviewType.NO_PREVIEW;
  }

  /**
   * @return The owner mod of this API implementation.
   * @since 1.3.0
   */
  String getModId();

  /**
   * Registers a preview provider for a list of items.
   * 
   * @param previewProviders A PreviewProviders to list of Items map.
   * @since 1.3.0
   */
  void registerProviders(Map<PreviewProvider, List<Item>> previewProviders);
}
