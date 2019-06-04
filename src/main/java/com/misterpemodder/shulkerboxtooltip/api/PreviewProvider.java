package com.misterpemodder.shulkerboxtooltip.api;

import java.util.List;
import javax.annotation.Nullable;
import com.misterpemodder.shulkerboxtooltip.impl.provider.EmptyPreviewProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

/**
 * Provides various infos about item preview such as the contained items.
 * @since 1.3.0
 */
public interface PreviewProvider {
  /**
   * A PreviewProvider that does nothing.
   * @since 1.3.0
   */
  static PreviewProvider EMPTY = EmptyPreviewProvider.INSTANCE;

  /**
   * The default inventory color.
   * @since 1.3.0
   */
  static float[] DEFAULT_COLOR = new float[] {0.592f, 0.403f, 0.592f};

  /**
   * @return The list of Items that uses this PreviewProvider.
   * @since 1.3.0
   */
  List<Item> getPreviewItems();

  /**
   * Fetches the items to be displayed in the preview.
   * @param stack The preview stack
   * @return The list of items, return {@code null} for no preview.
   * @since 1.3.0
   */
  @Nullable
  DefaultedList<ItemStack> getInventory(ItemStack stack);

  /**
   * Which color the preview window should be in?
   * @return An array of three floats (RGB). if {@code color.length < 3},
   * {@link #DEFAULT_COLOR} will be used.
   * @since 1.3.0
   */
  default float[] getWindowColor(ItemStack stack) {
    return DEFAULT_COLOR;
  }

  /**
   * @return A {@link PreviewRenderer} instance.
   * @since 1.3.0
   */
  default PreviewRenderer getRenderer() {
    return PreviewRenderer.getDefaultRendererInstance();
  }
}
