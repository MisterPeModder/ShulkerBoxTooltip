package com.misterpemodder.shulkerboxtooltip.impl.hook;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.Nullable;

/**
 * Provides access to the {@link net.minecraft.client.gui.GuiGraphicsExtractor} methods added by the mod.
 */
public interface GuiGraphicsExtensions {
  void setTooltipTopYPosition(int topY);

  int getTooltipTopYPosition();

  void setMouseX(int mouseX);

  int getMouseX();

  void setMouseY(int mouseY);

  int getMouseY();

  @Nullable Runnable getDeferredTooltip();

  void setDeferredTooltip(@Nullable Runnable deferredTooltip);

  static void renderTooltipImmediate(GuiGraphicsExtractor graphics, Runnable renderer) {
    var extendedGraphics = (GuiGraphicsExtensions) graphics;

    Runnable prevTooltip = extendedGraphics.getDeferredTooltip();
    extendedGraphics.setDeferredTooltip(null);

    renderer.run();

    Runnable currentTooltip = extendedGraphics.getDeferredTooltip();
    if (currentTooltip != null) {
      currentTooltip.run();
    }
    extendedGraphics.setDeferredTooltip(prevTooltip);
  }
}
