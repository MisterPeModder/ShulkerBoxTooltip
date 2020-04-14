package com.misterpemodder.shulkerboxtooltip.api.provider;

import java.util.Collections;
import java.util.List;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

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
  static float[] DEFAULT_COLOR = new float[] {1f, 1f, 1f};

  /**
   * Queries if the preview window should be displayed for the given context.
   * Should return {@code false} if the inventory if empty.
   * 
   * @param context The preview context.
   * @return Whether the preview should be displayed.
   * @since 2.0.0
   */
  default boolean shouldDisplay(PreviewContext context) {
    return this.shouldDisplay(context.getStack());
  }

  /**
   * Fetches the items to be displayed in the preview.
   * 
   * @param context The preview context.
   * @return The list of items, may not be null or contain null elements.
   * @since 2.0.0
   */
  default List<ItemStack> getInventory(PreviewContext context) {
    return this.getInventory(context.getStack());
  }

  /**
   * @param context The preview context.
   * @return The maximum inventory size for the given stack.
   * @since 2.0.0
   */
  default int getInventoryMaxSize(PreviewContext context) {
    return this.getInventoryMaxSize(context.getStack());
  }

  /**
   * The maximum number of item stacks to be displayed in a row.
   * 
   * @param context The preview context.
   * @return the row size, defaults to the max row size in config if 0.
   * @since 2.0.0
   */
  default int getMaxRowSize(PreviewContext context) {
    return this.getMaxRowSize(context.getStack());
  }

  /**
   * @param context The preview context.
   * @return If false, compact mode will be the only type of preview.
   * @since 2.0.0
   */
  default boolean isFullPreviewAvailable(PreviewContext context) {
    return this.isFullPreviewAvailable(context.getStack());
  }

  /**
   * Should hint be shown in the item's tooltip?
   * 
   * @param context The preview context.
   * @return whether the hints should be shown.
   * @since 2.0.0
   */
  default boolean showTooltipHints(PreviewContext context) {
    return this.showTooltipHints(context.getStack());
  }

  /**
   * @param context The preview context.
   * @return The text to be displayed for the compact preview mode.
   * @since 2.0.0
   */
  default String getTooltipHintLangKey(PreviewContext context) {
    return this.getTooltipHintLangKey(context.getStack());
  }

  /**
   * @param context The preview context.
   * @return The text to be displayed for the full preview mode.
   * @since 2.0.0
   */
  default String getFullTooltipHintLangKey(PreviewContext context) {
    return this.getFullTooltipHintLangKey(context.getStack());
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
    return this.getWindowColor(context.getStack());
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
    return this.addTooltip(context.getStack());
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
   * Queries if the preview window should be displayed for the given stack.
   * Should return {@code false} if the inventory if empty.
   * 
   * @param stack The stack.
   * @return Whether the preview should be displayed.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#shouldDisplay(PreviewContext)}.
   */
  @Deprecated
  default boolean shouldDisplay(ItemStack stack) {
    return false;
  }

  /**
   * Fetches the items to be displayed in the preview.
   * 
   * @param stack The preview stack
   * @return The list of items, may not be null or contain null elements.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#getInventory(PreviewContext)}.
   */
  @Deprecated
  default List<ItemStack> getInventory(ItemStack stack) {
    return Collections.emptyList();
  }

  /**
   * @param stack The stack.
   * @return The maximum inventory size for the given stack.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#getInventoryMaxSize(PreviewContext)}.
   */
  @Deprecated
  default int getInventoryMaxSize(ItemStack stack) {
    return 0;
  }

  /**
   * The maximum number of item stacks to be displayed in a row.
   * 
   * @param stack The stack.
   * @return the row size, defaults the max row size in config if 0.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#getMaxRowSize(PreviewContext)}.
   */
  @Deprecated
  default int getMaxRowSize(ItemStack stack) {
    return 0;
  }

  /**
   * @param stack The stack.
   * @return If false, compact mode will be the only type of preview.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#isFullPreviewAvailable(PreviewContext)}.
   */
  @Deprecated
  default boolean isFullPreviewAvailable(ItemStack stack) {
    return true;
  }

  /**
   * Should hint be shown in the item's tooltip?
   * 
   * @param stack The stack.
   * @return whether the hints should be shown.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#showTooltipHints(PreviewContext)}.
   */
  @Deprecated
  default boolean showTooltipHints(ItemStack stack) {
    return true;
  }

  /**
   * @param stack The stack.
   * @return The text to be displayed for the compact preview mode.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#getTooltipHintLangKey(PreviewContext)}.
   */
  @Deprecated
  default String getTooltipHintLangKey(ItemStack stack) {
    return "shulkerboxtooltip.hint.compact";
  }

  /**
   * @param stack The stack.
   * @return The text to be displayed for the full preview mode.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#getFullTooltipHintLangKey(PreviewContext)}.
   */
  @Deprecated
  default String getFullTooltipHintLangKey(ItemStack stack) {
    return "shulkerboxtooltip.hint.full";
  }

  /**
   * Which color the preview window should be in?
   * 
   * @param stack The stack.
   * @return An array of three floats (RGB). if {@code color.length < 3},
   * {@link #DEFAULT_COLOR} will be used.
   * @since 1.3.0
   * @deprecated Replaced with {@link PreviewProvider#getWindowColor(PreviewContext)}.
   */
  @Deprecated
  default float[] getWindowColor(ItemStack stack) {
    return DEFAULT_COLOR;
  }

  /**
   * Adds lines to the stack tooltip.
   * Returned lines are added only if tooltip type is set to {@code MODDED} in the config.
   * 
   * @param stack The stack.
   * @return A list of Text components. If empty, no text will be added to the tooltip.
   * @since 1.4.0
   * @deprecated Replaced with {@link PreviewProvider#addTooltip(PreviewContext)}.
   */
  @Deprecated
  default List<Text> addTooltip(ItemStack stack) {
    return Collections.emptyList();
  }
}
