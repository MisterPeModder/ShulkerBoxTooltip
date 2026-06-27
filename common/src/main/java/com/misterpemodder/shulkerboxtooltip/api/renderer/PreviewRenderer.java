package com.misterpemodder.shulkerboxtooltip.api.renderer;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.config.Theme;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.ModPreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.VanillaPreviewRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import javax.annotation.Nonnull;

/**
 * Renders a preview using a {@link PreviewProvider}.
 *
 * @since 1.3.0
 */
@Environment(EnvType.CLIENT)
public interface PreviewRenderer {
  /**
   * Returns the default renderer instance, corresponds the return value of either {@link #getModRendererInstance()},
   * or {@link #getVanillaRendererInstance()} depending on the mod's configuration.
   *
   * @return The instance of the default preview renderer.
   * @since 1.3.0
   */
  @Nonnull
  static PreviewRenderer getDefaultRendererInstance() {
    return getDefaultRendererInstance(ShulkerBoxTooltip.config.preview.theme);
  }

  /**
   * Returns the renderer instance for the given theme.
   *
   * @param theme The theme to use.
   * @return The renderer instance.
   * @since 5.3.0
   */
  @Nonnull
  static PreviewRenderer getDefaultRendererInstance(Theme theme) {
    return theme == Theme.VANILLA ? getVanillaRendererInstance() : getModRendererInstance();
  }

  /**
   * Returns the instance of ShulkerBoxTooltip's default preview renderer.
   *
   * @return The mod's default preview renderer.
   * @since 3.0.0
   */
  @Nonnull
  static PreviewRenderer getModRendererInstance() {
    return ModPreviewRenderer.INSTANCE;
  }

  /**
   * Returns an instance of ShulkerBoxTooltip's vanilla-style preview renderer.
   *
   * @return The mod's default preview renderer.
   * @since 3.0.0
   */
  @Nonnull
  static PreviewRenderer getVanillaRendererInstance() {
    return VanillaPreviewRenderer.INSTANCE;
  }

  /**
   * Gets the pixel height of the preview window.
   *
   * @return the height (in pixels) of the preview window.
   * @since 1.3.0
   */
  int getHeight();

  /**
   * Gets the pixel width of the preview window.
   *
   * @return the width (in pixels) of the preview window.
   * @since 1.3.0
   */
  int getWidth();

  /**
   * Sets the preview to use for the given context.
   *
   * @param context  The preview context.
   * @param provider The provider.
   * @since 2.0.0
   */
  void setPreview(PreviewContext context, PreviewProvider provider);

  /**
   * Sets the preview type.
   *
   * @param type The preview type.
   * @since 1.3.0
   */
  void setPreviewType(PreviewType type);

  /**
   * Renders the preview at the given coordinates.
   *
   * @param x        X position of the preview's upper-right corner.
   * @param y        Y position of the preview's upper-right corner.
   * @param graphics Context about the current matrices and more.
   * @param font     The text renderer.
   * @param mouseX   The X position of the mouse cursor, relative to the current active Screen.
   * @param mouseY   The Y position of the mouse cursor, relative to the current active Screen.
   * @since 4.0.0
   * @deprecated Use {@link #draw(int, int, int, int, GuiGraphicsExtractor, Font, int, int)} instead.
   */
  @Deprecated(forRemoval = true, since = "5.2.0")
  default void draw(int x, int y, GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  /**
   * Renders the preview at the given coordinates.
   *
   * @param x              X position of the preview's upper-right corner.
   * @param y              Y position of the preview's upper-right corner.
   * @param viewportWidth  Number of pixels available for rendering the preview in the X axis.
   * @param viewportHeight Number of pixels available for rendering the preview in the Y axis.
   * @param graphics       Context about the current matrices and more.
   * @param font           The text renderer.
   * @param mouseX         The X position of the mouse cursor, relative to the current active Screen.
   * @param mouseY         The Y position of the mouse cursor, relative to the current active Screen.
   * @since 5.2.0
   */
  @Deprecated(forRemoval = true, since = "5.4.0")
  default void draw(int x, int y, int viewportWidth, int viewportHeight, GuiGraphicsExtractor graphics, Font font,
      int mouseX, int mouseY) {
    this.draw(x, y, graphics, font, mouseX, mouseY);
  }

  /**
   * Renders the preview.
   *
   * @param context The context.
   * @since 5.4.0
   */
  default void draw(RenderContext context) {
    this.draw(context.x(), context.y(), context.viewportWidth(), context.viewportHeight(), context.graphics(),
        context.font(), context.mouseX(), context.mouseY());
  }

  /**
   * Returns the X-axis offset applied to the preview when it is rendered outside the tooltip
   * (i.e. when the preview position is {@code OUTSIDE_TOP} or {@code OUTSIDE_BOTTOM}).
   * <p>
   * The default implementation returns {@code 0} (no adjustment).
   *
   * @return the X offset in pixels.
   * @since 5.4.0
   */
  default int getOutsideXOffset() {
    return 0;
  }

  /**
   * Returns the Y-axis offset between the tooltip and the preview when it is rendered outside the
   * tooltip (i.e. when the preview position is {@code OUTSIDE_TOP} or {@code OUTSIDE_BOTTOM}).
   * <p>
   * The default implementation returns {@code 0} (no adjustment).
   *
   * @return the Y offset in pixels.
   * @since 5.4.0
   */
  default int getOutsideYOffset() {
    return 0;
  }
}
