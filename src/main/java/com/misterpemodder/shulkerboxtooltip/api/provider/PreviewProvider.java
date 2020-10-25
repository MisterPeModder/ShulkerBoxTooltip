package com.misterpemodder.shulkerboxtooltip.api.provider;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Provides various infos about item preview such as the contained items.
 * 
 * @since 1.3.0
 */
public interface PreviewProvider {
  /**
   * The default inventory color.
   * 
   * @since 1.3.0
   */
  static float[] DEFAULT_COLOR = new float[] { 1f, 1f, 1f };

  /**
   * Queries if the preview window should be displayed for the given context.
   * Should return {@code false} if the inventory if empty.
   * 
   * @param context The preview context.
   * @return Whether the preview should be displayed.
   * @since 2.0.0
   */
  default boolean shouldDisplay(PreviewContext context) {
    return false;
  }

  /**
   * Fetches the items to be displayed in the preview.
   * 
   * @param context The preview context.
   * @return The list of items, may not be null or contain null elements.
   * @since 2.0.0
   */
  default List<ItemStack> getInventory(PreviewContext context) {
    return Collections.emptyList();
  }

  /**
   * @param context The preview context.
   * @return The maximum inventory size for the given stack.
   * @since 2.0.0
   */
  default int getInventoryMaxSize(PreviewContext context) {
    return 0;
  }

  /**
   * The maximum number of item stacks to be displayed in a row.
   * 
   * @param context The preview context.
   * @return the row size, defaults to the max row size in config if 0.
   * @since 2.0.0
   */
  default int getMaxRowSize(PreviewContext context) {
    return 0;
  }

  /**
   * @param context The preview context.
   * @return If false, compact mode will be the only type of preview.
   * @since 2.0.0
   */
  default boolean isFullPreviewAvailable(PreviewContext context) {
    return true;
  }

  /**
   * Should hint be shown in the item's tooltip?
   * 
   * @param context The preview context.
   * @return whether the hints should be shown.
   * @since 2.0.0
   */
  default boolean showTooltipHints(PreviewContext context) {
    return true;
  }

  /**
   * @param context The preview context.
   * @return The text to be displayed for the compact preview mode.
   * @since 2.0.0
   */
  default String getTooltipHintLangKey(PreviewContext context) {
    return "shulkerboxtooltip.hint.compact";
  }

  /**
   * @param context The preview context.
   * @return The text to be displayed for the full preview mode.
   * @since 2.0.0
   */
  default String getFullTooltipHintLangKey(PreviewContext context) {
    return "shulkerboxtooltip.hint.full";
  }

  /**
   * Which color the preview window should be in?
   * 
   * @param context The preview context.
   * @return An array of three floats (RGB). if {@code color.length < 3},
   * {@link #DEFAULT_COLOR} will be used.
   * @since 2.0.0
   */
  default float[] getWindowColor(PreviewContext context) {
    return DEFAULT_COLOR;
  }

  /**
   * @return A {@link PreviewRenderer} instance.
   * @since 1.3.0
   */
  @Environment(EnvType.CLIENT)
  default PreviewRenderer getRenderer() {
    return PreviewRenderer.getDefaultRendererInstance();
  }

  /**
   * Adds lines the stack tooltip.
   * Returned lines are added only if tooltip type is set to {@code MODDED} in the config.
   * 
   * @param context The preview context.
   * @return A list of Text components. If empty, no text will be added to the tooltip.
   * @since 2.0.0
   */
  default List<Text> addTooltip(PreviewContext context) {
    return Collections.emptyList();
  }

  /**
   * This method should be called every time the inventory of the stack starts being accessed
   * (i.e by hovering it).
   * 
   * @param context The preview context.
   * @since 2.0.0
   */
  @Environment(EnvType.CLIENT)
  default void onInventoryAccessStart(PreviewContext context) {
  }

  /**
   * Overrides the texture used to display the preview window.
   * 
   * @param context The preview context.
   * @return The texure path, or null for the default texture.
   * @since 2.2.0
   */
  @Nullable
  @Environment(EnvType.CLIENT)
  default Identifier getTextureOverride(PreviewContext context) {
    return null;
  }

  /**
   * Priority of this preview provider, relative to other providers targetting the same item.
   * The provider that returns the highest number will be chosen, in case the priorities are equal,
   * the provider will be chosen arbitrarily chosen.
   * 
   * @return The priority of this preview provider.
   * @since 2.3.0
   */
  default int getPriority() {
    return 1000;
  }
}
