package com.misterpemodder.shulkerboxtooltip.api.renderer;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.Theme;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.ModPreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.VanillaPreviewRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;

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
    return ShulkerBoxTooltip.config.preview.theme == Theme.VANILLA ? getVanillaRendererInstance()
        : getModRendererInstance();
  }

  /**
   * Returns the instance of ShulkerBoxTooltip's default preview renderer.
   * @return The mod's default preview renderer.
   * @since 3.0.0
   */
  static PreviewRenderer getModRendererInstance() {
    return ModPreviewRenderer.INSTANCE;
  }

  /**
   * Returns an instance of ShulkerBoxTooltip's vanilla-style preview renderer. 
   * @return The mod's default preview renderer.
   * @since 3.0.0
   */
  static PreviewRenderer getVanillaRendererInstance() {
    return VanillaPreviewRenderer.INSTANCE;
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
   * @param z The depth of the preview
   * @param matrices
   * @param textRenderer
   * @param textureManager
   * @since 3.0.0
   */
  void draw(int x, int y, int z, MatrixStack matrices, TextRenderer textRenderer,
      ItemRenderer itemRenderer, TextureManager textureManager);
}
