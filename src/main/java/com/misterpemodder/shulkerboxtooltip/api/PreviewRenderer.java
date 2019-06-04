package com.misterpemodder.shulkerboxtooltip.api;

import com.misterpemodder.shulkerboxtooltip.impl.DefaultPreviewRenderer;
import net.minecraft.item.ItemStack;

/**
 * Renders a preview using a {@link PreviewProvider}.
 * @since 1.3.0
 */
public interface PreviewRenderer {
  /**
   * Creates an instance of the default preview renderer
   * @return The preview renderer instance.
   * @since 1.3.0
   */
  static PreviewRenderer getDefaultRendererInstance() {
    return new DefaultPreviewRenderer();
  }

  /**
   * @return the height (in pixels) of the preview window.
   * @since 1.3.0
   */
  int getHeight();

  /**
   * @return the width (in pixels) of the preview window.
   * @since 1.3.0
   */
  int getWidth();

  /**
   * Sets the preview to use for the given ItemStack.
   * @since 1.3.0
   */
  void setPreview(ItemStack stack, PreviewProvider provider);

  /**
   * Sets the preview type.
   * @since 1.3.0
   */
  void setPreviewType(PreviewType type);

  /**
   * Renders the preview at the given coordinates.
   * @since 1.3.0
   */
  void draw(int x, int y);
}
