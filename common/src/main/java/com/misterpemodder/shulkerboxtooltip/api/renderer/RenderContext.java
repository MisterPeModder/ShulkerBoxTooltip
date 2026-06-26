package com.misterpemodder.shulkerboxtooltip.api.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import javax.annotation.Nonnull;

/**
 * Parameters of {@link PreviewRenderer#draw(RenderContext)}.
 *
 * @since 5.4.0
 */
@Environment(EnvType.CLIENT)
public interface RenderContext {
  /**
   * X position of the preview's upper-right corner.
   *
   * @since 5.4.0
   */
  int x();

  /**
   * Y position of the preview's upper-right corner.
   *
   * @since 5.4.0
   */
  int y();

  /**
   * Number of pixels available for rendering the preview in the X axis.
   *
   * @since 5.4.0
   */
  int viewportWidth();

  /**
   * Number of pixels available for rendering the preview in the Y axis.
   *
   * @since 5.4.0
   */
  int viewportHeight();

  /**
   * Context about the current matrices and more.
   *
   * @since 5.4.0
   */
  @Nonnull
  GuiGraphicsExtractor graphics();

  /**
   * The text renderer.
   *
   * @since 5.4.0
   */
  @Nonnull
  Font font();

  /**
   * The X position of the mouse cursor, relative to the current active Screen.
   *
   * @since 5.4.0
   */
  int mouseX();

  /**
   * The Y position of the mouse cursor, relative to the current active Screen.
   *
   * @since 5.4.0
   */
  int mouseY();

  /**
   * X position of the tooltip's upper-right corner.
   *
   * @since 5.4.0
   */
  int tooltipTopX();

  /**
   * Y position of the tooltip's upper-right corner.
   *
   * @since 5.4.0
   */
  int tooltipTopY();
}
