package com.misterpemodder.shulkerboxtooltip.api.renderer;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.DefaultPreviewRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Renders a preview using a {@link PreviewProvider}.
 * @since 1.3.0
 */
@Environment(EnvType.CLIENT)
public interface PreviewRenderer {
  /**
   * @return The instance of the default preview renderer.
   * @since 1.3.0
   */
  static PreviewRenderer getDefaultRendererInstance() {
    return DefaultPreviewRenderer.INSTANCE;
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
   * Sets the preview to use for the given context.
   * @param context  The preview context.
   * @param provider The provider.
   * @since 2.0.0
   */
  void setPreview(PreviewContext context, PreviewProvider provider);

  /**
   * Sets the preview type.
   * @param type The preview type.
   * @since 1.3.0
   */
  void setPreviewType(PreviewType type);

  /**
   * Renders the preview at the given coordinates.
   * @param x X position of the preview's upper-right corner
   * @param y Y position of the preview's upper-right corner
   * @since 1.3.0
   */
  void draw(int x, int y);
}
