package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

public abstract class PositionAwareTooltipComponent implements TooltipComponent {
  public abstract void drawItemsWithTooltipPosition(TextRenderer textRenderer, int x, int y, DrawContext context,
      int tooltipTopY, int tooltipBottomY, int mouseX, int mouseY);

  public abstract void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context);

  /**
   * Fallback in case the 1.20-like API gets bypassed.
   */
  @Override
  public final void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer,
      int z) {
    DrawContext drawContext = new DrawContext(null);
    drawContext.update(matrices, itemRenderer);
    drawContext.setZ(z);
    this.drawItems(textRenderer, x, y, drawContext);
  }
}
