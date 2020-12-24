package com.misterpemodder.shulkerboxtooltip.api;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetwoking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Implement this interface and use this as your entrypoint.
 * @since 1.3.0
 */
public interface ShulkerBoxTooltipApi {
  /**
   * @param stack The stack
   * @return the associated {@link PreviewProvider} for the passed {@link ItemStack}.
   * @since 2.0.0
   */
  @Nullable
  static PreviewProvider getPreviewProviderForStack(ItemStack stack) {
    Map<Item, PreviewProvider> previewItems = ShulkerBoxTooltip.getPreviewItems();

    return previewItems == null ? null : previewItems.get(stack.getItem());
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
    if (ShulkerBoxTooltip.config.main.enablePreview) {
      PreviewProvider provider = getPreviewProviderForStack(context.getStack());

      return provider != null && provider.shouldDisplay(context) && ShulkerBoxTooltipApi
          .getCurrentPreviewType(provider.isFullPreviewAvailable(context)) != PreviewType.NO_PREVIEW;
    }
    return false;
  }

  /**
   * @param hasFullPreviewMode Is the full preview mode available?
   * @return The shulker box tooltip type depending of which keys are pressed.
   * @since 2.0.0
   */
  @Environment(EnvType.CLIENT)
  static PreviewType getCurrentPreviewType(boolean hasFullPreviewMode) {
    boolean shouldDisplay = ShulkerBoxTooltipClient.shouldDisplayPreview();

    if (shouldDisplay && !hasFullPreviewMode) {
      return PreviewType.COMPACT;
    }
    if (ShulkerBoxTooltip.config.main.swapModes) {
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
    if (ShulkerBoxTooltip.config.controls.previewKey == null)
      return false;
    return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),
        ShulkerBoxTooltip.config.controls.previewKey.get().getCode());
  }

  /**
   * @return true if the full preview key ({@code alt} by default) is pressed.
   * @since 2.1.0
   */
  @Environment(EnvType.CLIENT)
  static boolean isFullPreviewKeyPressed() {
    if (ShulkerBoxTooltip.config.controls.fullPreviewKey == null)
      return false;
    return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),
        ShulkerBoxTooltip.config.controls.fullPreviewKey.get().getCode());
  }

  /**
   * @param player The player.
  * @return true if the player has the mod installed and server integration turned on.
  * @since 2.0.0
  */
  static boolean hasModAvailable(ServerPlayerEntity player) {
    return ServerNetwoking.hasModAvailable(player);
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
