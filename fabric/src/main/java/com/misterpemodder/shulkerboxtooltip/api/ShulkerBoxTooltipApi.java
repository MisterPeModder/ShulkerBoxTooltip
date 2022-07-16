package com.misterpemodder.shulkerboxtooltip.api;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import com.misterpemodder.shulkerboxtooltip.fabric.ShulkerBoxTooltipClientFabric;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.annotation.Nullable;

/**
 * Implement this interface and use this as your entrypoint.
 * @since 1.3.0
 */
public interface ShulkerBoxTooltipApi {
  /**
   * @param stack The stack
   * @return the associated {@link PreviewProvider} for the passed {@linkplain ItemStack}.
   * @since 2.0.0
   */
  @Nullable
  static PreviewProvider getPreviewProviderForStack(ItemStack stack) {
    return PreviewProviderRegistry.getInstance().get(stack);
  }

  /**
   * Is there a preview available for the given preview context?
   * 
   * @param context The preview context.
   * @return true if there is a preview available
   * @since 2.0.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isPreviewAvailable(PreviewContext context) {
    if (ShulkerBoxTooltip.config.preview.enable) {
      PreviewProvider provider = getPreviewProviderForStack(context.getStack());

      return provider != null && provider.shouldDisplay(context)
          && ShulkerBoxTooltipApi.getCurrentPreviewType(
              provider.isFullPreviewAvailable(context)) != PreviewType.NO_PREVIEW;
    }
    return false;
  }

  /**
   * @param hasFullPreviewMode Is the full preview mode available?
   * @return The shulker box tooltip type depending on which keys are pressed.
   * @since 2.0.0
   */
  @Environment(EnvType.CLIENT)
  static PreviewType getCurrentPreviewType(boolean hasFullPreviewMode) {
    boolean shouldDisplay = ShulkerBoxTooltipClientFabric.shouldDisplayPreview();

    if (shouldDisplay && !hasFullPreviewMode) {
      return PreviewType.COMPACT;
    }
    if (ShulkerBoxTooltip.config.preview.swapModes) {
      if (shouldDisplay)
        return isFullPreviewKeyPressed() ? PreviewType.COMPACT : PreviewType.FULL;
    } else {
      if (shouldDisplay)
        return isFullPreviewKeyPressed() ? PreviewType.FULL : PreviewType.COMPACT;
    }
    return PreviewType.NO_PREVIEW;
  }

  /**
   * @return true if the preview key ({@code shift} by default) is pressed.
   * @since 2.1.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isPreviewKeyPressed() {
    return ShulkerBoxTooltipClientFabric.isPreviewKeyPressed();
  }

  /**
   * @return true if the full preview key ({@code alt} by default) is pressed.
   * @since 2.1.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isFullPreviewKeyPressed() {
    return ShulkerBoxTooltipClientFabric.isFullPreviewKeyPressed();
  }

  /**
   * @param player The player.
   * @return true if the player has the mod installed and server integration turned on.
   * @since 2.0.0
   */
  static boolean hasModAvailable(ServerPlayerEntity player) {
    return ServerNetworking.hasModAvailable(player);
  }

  /**
   * Called on each entrypoint to register preview providers.
   * 
   * @param registry The registry.
   * @since 3.0.0
   */
  void registerProviders(PreviewProviderRegistry registry);
}
