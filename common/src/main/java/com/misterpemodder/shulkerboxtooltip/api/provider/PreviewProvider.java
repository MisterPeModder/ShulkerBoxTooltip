package com.misterpemodder.shulkerboxtooltip.api.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.config.Theme;
import com.misterpemodder.shulkerboxtooltip.api.renderer.PreviewRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Describes preview properties for a registered set of items.
 *
 * @since 1.3.0
 */
public interface PreviewProvider {
  /**
   * Queries if the preview window should be displayed for the given context.
   * Should return {@code false} if the inventory is empty.
   *
   * @param context The preview context.
   * @return Whether the preview should be displayed.
   * @since 2.0.0
   */
  boolean shouldDisplay(PreviewContext context);

  /**
   * Fetches the items to be displayed in the preview.
   *
   * @param context The preview context.
   * @return The list of items, may not be null or contain null elements.
   * @since 2.0.0
   */
  List<ItemStack> getInventory(PreviewContext context);

  /**
   * Returns the maximum amount of slots this preview can display.
   *
   * @param context The preview context.
   * @return The maximum inventory size for the given stack.
   * @since 2.0.0
   */
  int getInventoryMaxSize(PreviewContext context);

  /**
   * Returns the number of actively usable slots in the preview.
   *
   * @param context The preview context.
   * @return The number of active (usable) slots, default is {@link #getInventoryMaxSize}.
   * @since ?.?.?
   */
  default int getActiveSlotCount(PreviewContext context) {
    return getInventoryMaxSize(context);
  }

  /**
   * The maximum number of item stacks to be displayed in a row in full preview mode.
   *
   * @param context The preview context.
   * @return the row size, defaults to the max row size in config if 0.
   * @since 2.0.0
   */
  default int getMaxRowSize(PreviewContext context) {
    return 0;
  }

  /**
   * The maximum number of item stacks to be displayed in a row in compact preview mode.
   *
   * @param context The preview context.
   * @return the row size in compact mode, defaults to the max row size in config if 0.
   * @since 5.2.0
   */
  default int getCompactMaxRowSize(PreviewContext context) {
    return 0;
  }

  /**
   * Returns whether this provider supports full preview mode.
   *
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
   * The translation key for a hint that tells the user how to enter compact preview mode.
   *
   * @param context The preview context.
   * @return The text to be displayed for the compact preview mode.
   * @since 2.0.0
   */
  default String getTooltipHintLangKey(PreviewContext context) {
    return "shulkerboxtooltip.hint.compact";
  }

  /**
   * The translation key for a hint that tells the user how to enter full preview mode.
   *
   * @param context The preview context.
   * @return The text to be displayed for the full preview mode.
   * @since 2.0.0
   */
  default String getFullTooltipHintLangKey(PreviewContext context) {
    return "shulkerboxtooltip.hint.full";
  }

  /**
   * The translation key for a hint that tells the user how to lock the tooltip window.
   *
   * @param context The preview context.
   * @return The text to be displayed for the lock tooltip hint.
   * @since 3.4.0
   */
  default String getLockKeyTooltipHintLangKey(PreviewContext context) {
    return "shulkerboxtooltip.hint.lock";
  }

  /**
   * Which color the preview window should be in?
   *
   * @param context The preview context.
   * @return The desired ColorKey instance.
   * @since 3.2.0
   */
  @Environment(EnvType.CLIENT)
  default ColorKey getWindowColorKey(PreviewContext context) {
    return ColorKey.DEFAULT;
  }

  /**
   * Returns the theme to use for this provider.
   *
   * @return The theme to use.
   * @since ?.?.?
   */
  @Environment(EnvType.CLIENT)
  default Theme getTheme() {
    return ShulkerBoxTooltip.config.preview.theme;
  }

  /**
   * Get the renderer to use for this type of preview.
   *
   * @return A {@link PreviewRenderer} instance.
   * @since 1.3.0
   */
  @Environment(EnvType.CLIENT)
  default PreviewRenderer getRenderer() {
    return PreviewRenderer.getDefaultRendererInstance(this.getTheme());
  }

  /**
   * Adds lines the stack tooltip.
   * Returned lines are added only if tooltip type is set to {@code MODDED} in the config.
   *
   * @param context The preview context.
   * @return A list of Text components. If empty, no text will be added to the tooltip.
   * @since 2.0.0
   */
  default List<Component> addTooltip(PreviewContext context) {
    return Collections.emptyList();
  }

  /**
   * This method should be called every time the inventory of the stack starts being accessed
   * (i.e. by hovering it).
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
   * @return The texture path, or null for the default texture.
   * @since 2.2.0
   */
  @Nullable
  @Environment(EnvType.CLIENT)
  default Identifier getTextureOverride(PreviewContext context) {
    return null;
  }

  /**
   * Priority of this preview provider, relative to other providers targeting the same item.
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
